package org.openmrs.module.messaging.sms.util;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;


/**
 * A commandline utility for detecting connected AT devices.
 * The detection workflow was taken from ComTest in SMSLib.
 * @author Alex Anderson alex@frontlinesms.com
 */
public class AllModemsDetector{
	public static void main(String[] args) {
		AllModemsDetector amd = new AllModemsDetector();
		ATDeviceDetector[] detectors = amd.detectBlocking();
		detectModems(detectors);
	}
	
	public static Service getService(){
		AllModemsDetector amd = new AllModemsDetector();
		ATDeviceDetector[] detectors = amd.detectBlocking();
		return detectModems(detectors);
	}
	
	protected static Log log = LogFactory.getLog(AllModemsDetector.class);
	
	private ATDeviceDetector[] detectors;
	
	/** Trigger detection, and return the results when it is completed. */
	public ATDeviceDetector[] detectBlocking() {
		detect();
		waitUntilDetectionComplete(detectors);
		return getDetectors();
	}
	
	/** Trigger detection. */
	public void detect() {
		log.trace("Starting device detection...");
		Set<ATDeviceDetector> detectors = new HashSet<ATDeviceDetector>();
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ATDeviceDetector d = new ATDeviceDetector(port);
				detectors.add(d);
				d.start();
			} else {
				log.info("Ignoring non-serial port: " + port.getName());
			}
		}
		this.detectors = detectors.toArray(new ATDeviceDetector[0]);
		log.info("All detectors started.");
	}
	
	/** Get the detectors. */
	public ATDeviceDetector[] getDetectors() {
		return detectors;
	}
	
	/** Blocks until all detectors have completed execution. */
	private static void waitUntilDetectionComplete(ATDeviceDetector[] detectors) {
		boolean completed;
		do {
			completed = true;
			for (ATDeviceDetector portDetector : detectors) {
				if(!portDetector.isFinished()) {
					completed = false;
				}
			}
			Utils.sleep(500);
		} while(!completed);
	}
	
	/** Prints a report to {@link System#out} detailing the devices that were detected. */
	protected static Service detectModems(ATDeviceDetector[] completedDetectors) {
		// All detectors are finished, so print a report
		Service s = new Service();
		//if the system is a linux system, turn on serial polling
		if(System.getProperty("os.name").contains("nux")){
			log.info("Messaging: computer is running linux. To make RXTX work properly, we are turning on serial polling");
			s.getSettings().SERIAL_POLLING=true;
		}
		for(ATDeviceDetector d : completedDetectors) {
			log.info("----");
			log.info("PORT   : " + d.getPortIdentifier().getName());
			if(d.isDetected()) {
				log.info("SERIAL : " + d.getSerial());
				log.info("BAUD   : " + d.getMaxBaudRate());
				SerialModemGateway smg = new SerialModemGateway(d.getPortIdentifier().getName(),d.getPortIdentifier().getName(), d.getMaxBaudRate(), "Motorola", "RAZR V3re");
				try {
					s.addGateway(smg);
				} catch (Exception e) {
					log.info("Unable to add modem gateway to service\n"+e);
				}
				smg.setInbound(true);
				smg.setOutbound(true);
			} else {
				log.info("DETECTION FAILED \n"+d.getExceptionMessage());
			}
		}
		if(s.getGateways().size() !=0){
			log.info("Attempting to start service");
			try {
				s.startService();
				log.info("Service started successfully");
			} catch (Exception e) {
				log.info("Unable to start service" + e.getStackTrace());
			}
			try {
				s.queueMessage(new OutboundMessage("+18064702422","Hello, world. My name is OASYS"));
			}catch(Exception e){
				log.info("Unable to send test message\n"+e.getStackTrace());
			}
		}
		return s;
	}
}

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
	
	private String exceptionMessage;
	
	public ATDeviceDetector(CommPortIdentifier port) {
		super("ATDeviceDetector: " + port.getName());
		this.portIdentifier = port;
	}
	
	public void run() {
		for(int baud : BAUD_RATES) {
			SerialPort serialPort = null;
			InputStream in = null;
			OutputStream out = null;
			try {
				try{
					serialPort = (SerialPort) portIdentifier.open("ATDeviceDetector", 2000);
				}catch(Exception e){
					log.info("Port in use",e);
					break;
				}
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
				serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
				serialPort.enableReceiveTimeout(1000);
				
				log.info("LOOPING.");
				
				// discard all data currently waiting on the input stream
				Utils.readAll(in);
				Utils.writeCommand(out, "AT");
				Utils.sleep(1000);
				String response = Utils.readAll(in);
				if(!Utils.isResponseOk(response)) {
					throw new ATDeviceDetectionException("Bad response: " + response);
				} else {
					Utils.writeCommand(out, "AT+CGSN");
					response = Utils.readAll(in);
					if(!Utils.isResponseOk(response)) {
						throw new ATDeviceDetectionException("Bad response to request for serial number: " + response);
					} else {
						String serial = Utils.trimResponse("AT+CGSN", response);
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
				log.info("Problem connecting to device.", ex);
				this.exceptionMessage = ex.getMessage();
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
	
	public String getExceptionMessage() {
		assert(!isDetected()) : "Cannot get Throwable clause if device was detected successfully.";
		return exceptionMessage;
	}
}

class Utils {
	/** Calls {@link Thread#sleep(long)} and ignores {@link InterruptedException}s thrown. */
	public static final void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException ex) {
			// ignore
		}
	}
	
	/**
	 * Read all bytes available on the input stream, and concatenates them into a string.
	 * N.B. This casts bytes read directly to characters.
	 */
	public static final String readAll(InputStream in) throws IOException {
		StringBuilder bob = new StringBuilder();
		int c;
		while((c = in.read()) != -1) {
			bob.append((char) c);
		}
		return bob.toString();
	}
	
	/** Writes the supplied command to the output stream, followed by a \r character */
	public static final void writeCommand(OutputStream out, String command) throws IOException {
		for(char c : command.toCharArray()) out.write(c);
		out.write('\r');
	}
	
	/** @return <code>true</code> if the response contains "OK" */
	public static final boolean isResponseOk(String response) {
		return response.indexOf("OK") != -1;
	}
	
	/** @return the response with the original command, "OK" and all trailing and leading whitespace removed */
	public static final String trimResponse(String command, String response) {
		return response.replace("OK", "").replace(command, "").trim();
	}
}

/** Exception thrown when detecting an AT device. */
@SuppressWarnings("serial")
class ATDeviceDetectionException extends Exception {
	public ATDeviceDetectionException(String message) {
		super(message);
	}
}