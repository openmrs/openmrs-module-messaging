package org.openmrs.module.messaging.sms.servicemanager;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class ATDeviceDetector extends Thread {
	/** Valid baud rates */
	private static final int[] BAUD_RATES = { 9600, 14400, 19200, 28800, 33600, 38400, 56000, 57600, 115200, 230400, 460800, 921600 };

	/** Logger */
	private final Log log = LogFactory.getLog(ATDeviceDetector.class);
	/** Port this is detecting on */
	private final CommPortIdentifier portIdentifier;
	/** The top speed the device was detected at. */
	private int maxBaudRate;
	/** The serial number of the detected device. */
	private String serial;
	/** <code>true</code> when the detection thread has finished. */
	private boolean finished;
	/** the phone number of this modem**/
	private String number;
	
	public String getNumber() {
		return number;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	private String manufacturer;
	
	private String model;
	
	private Exception exceptionMessage;
	
	public ATDeviceDetector(CommPortIdentifier port) {
		super("ATDeviceDetector: " + port.getName());
		this.portIdentifier = port;
	}
	
	public void run() {
		log.info("Beginning detection on port " + portIdentifier);
		for(int baud : BAUD_RATES) {
			SerialPort serialPort = null;
			InputStream in = null;
			OutputStream out = null;
			try {
				try{
					serialPort = (SerialPort) portIdentifier.open("ATDeviceDetector", 2000);
				}catch(Exception e){
					log.info("Port in use",e);
					this.exceptionMessage = e;
					break;
				}
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
				serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
				serialPort.enableReceiveTimeout(1000);
				
				// discard all data currently waiting on the input stream
				DetectorUtils.readAll(in);
				DetectorUtils.writeCommand(out, "AT");
				DetectorUtils.sleep(1000);
				String response = DetectorUtils.readAll(in);
				if(!DetectorUtils.isResponseOk(response)) {
					throw new ATDeviceDetectionException("Bad response: " + response);
				} else {
					DetectorUtils.writeCommand(out, "AT+CGSN");
					response = DetectorUtils.readAll(in);
					if(!DetectorUtils.isResponseOk(response)) {
						throw new ATDeviceDetectionException("Bad response to request for serial number: " + response);
					} else {
						String serial = DetectorUtils.trimResponse("AT+CGSN", response);
						log.info("Found serial: " + serial);
						if(this.serial != null) {
							// There was already a serial detected.  Check if it's the same as
							// what we've just got.
							if(!this.serial.equals(serial)) {
								log.info("New serial detected: '" + serial + "'.  Replacing previous: '" + this.serial + "'");
							}
						}
						this.serial = serial;
						maxBaudRate = Math.max(maxBaudRate, baud);
					}
				}
			} catch(Exception ex) {
				log.error("Problem connecting to device.", ex);
				this.exceptionMessage = ex;
			} finally {
				// Close any open streams
				if(out != null) try { out.close(); } catch(Exception ex) { log.warn("Error closing output stream.", ex); }
				if(in != null) try { in.close(); } catch(Exception ex) { log.warn("Error closing input stream.", ex); }
				if(serialPort != null) try { serialPort.close(); } catch(Exception ex) { log.warn("Error closing serial port.", ex); }
			}
		}
		finished = true;
		log.info("Detection completed on port: " + this.portIdentifier.getName());
	}
	
//> ACCESSORS
	public boolean isFinished() {
		return finished;
	}
	
	public boolean isDetected() {
		return this.maxBaudRate > 0;
	}
	
	public CommPortIdentifier getPortIdentifier() {
		return portIdentifier;
	}
	
	public int getMaxBaudRate() {
		return maxBaudRate;
	}
	
	public String getSerial() {
		assert(isDetected()) : "Cannot get serial if no device was detected.";
		return serial;
	}
	
	public Exception getException() {
		return exceptionMessage;
	}
}