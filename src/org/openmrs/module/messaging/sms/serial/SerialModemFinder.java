package org.openmrs.module.messaging.sms.serial;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.helper.CommPortIdentifier;
import org.smslib.helper.SerialPort;
import org.smslib.modem.SerialModemGateway;

/**
 * @author Dieterich
 *
 */
public class SerialModemFinder {
	
	private static Log log = LogFactory.getLog(SerialModemFinder.class);
	
	private final static Formatter _formatter = new Formatter(System.out);

	static CommPortIdentifier portId;

	static Enumeration<CommPortIdentifier> portList;

	static int bauds[] = { 9600, 14400, 19200, 28800, 33600, 38400, 56000,
			57600, 115200 };

	/**
	 * Wrapper around {@link CommPortIdentifier#getPortIdentifiers()} to be
	 * avoid unchecked warnings.
	 */
	private static Enumeration<CommPortIdentifier> getCleanPortIdentifiers() {
		return CommPortIdentifier.getPortIdentifiers();
	}

	/**
	 * Returns a list of all serial modems attached to the system that respond
	 * to AT commands
	 * @return
	 */
	public static List<SerialModemGateway> getModemGateways() {
		ArrayList<SerialModemGateway> devices = new ArrayList<SerialModemGateway>();
		log.debug("Searching for devices...");
		//iterate through all ports
		portList = getCleanPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			//only check it if it's a serial port
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				log.debug("Found serial port: " + portId.getName());
				//try the different bauds
				for (int i = 0; i < bauds.length; i++) {
					SerialPort serialPort = null;
					log.debug("       Trying at " + bauds[i] + " baud...");

					try {
						InputStream inStream;
						OutputStream outStream;
						int c;
						String response;
						serialPort = portId.open("SMSLibCommTester", 1971);
						serialPort
								.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
						serialPort.setSerialPortParams(bauds[i],
								SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
						inStream = serialPort.getInputStream();
						outStream = serialPort.getOutputStream();
						serialPort.enableReceiveTimeout(1000);
						c = inStream.read();
						while (c != -1)
							c = inStream.read();
						//see if it responds to the AT command
						outStream.write('A');
						outStream.write('T');
						outStream.write('\r');
						Thread.sleep(1000);
						response = "";
						c = inStream.read();
						while (c != -1) {
							response += (char) c;
							c = inStream.read();
						}
						//if it responds, create a gateway and add it to the list
						if (response.indexOf("OK") >= 0) {
							devices.add(initializeGateway(portId,bauds[i],inStream,outStream));
						} else {
							log.debug("Device unresponsive");
						}
					} catch (Exception e) {
						log.debug("Unable to connect to device",e);
					} finally {
						if (serialPort != null) {
							serialPort.close();
						}
					}
				}
			}
		}
		log.debug("\nTest complete.");
		return devices;
	}

	/**
	 * Issues a command, char by char, to the provided output stream and listens for a respons on the
	 * provided inputStream
	 * @param command
	 * @param outStream
	 * @param inStream
	 * @return the response to the input stream, if any
	 * @throws Exception
	 */
	public static String issueCommand(String command, OutputStream outStream, InputStream inStream) throws Exception {
		String result = "";
		//write the command to the output stream
		for (char c : command.toCharArray()) {
			outStream.write(c);
		}
		//terminate the stream
		outStream.write('\r');
		//read the response
		int c = inStream.read();
		while (c != -1) {
			result += (char) c;
			c = inStream.read();
		}
		return result;
	}

	/**
	 * Creates a gateway from the provided parameters
	 * @param portId
	 * @param baud
	 * @param inStream
	 * @param outStream
	 * @return
	 */
	public static SerialModemGateway initializeGateway( CommPortIdentifier portId, int baud, InputStream inStream, OutputStream outStream) {
		String id = portId.getName();
		String manufacturer = "";
		String model = "";

		// attempt to get the manufacturer info by issuing
		// the AT+CGMI command
		try {
			manufacturer = issueCommand("AT+CGMI",outStream,inStream);
		} catch (Exception e) {
			log.debug("Unable to get manufacturer");
		}

		// attempt to get the manufacturer info by issuing
		// the AT+CGMM command
		try {
			model= issueCommand("AT+CGMM",outStream,inStream);
		} catch (Exception e) {
			log.debug("Unable to get model");
		}

		return new SerialModemGateway(id,id,baud,manufacturer,model);
	}
}
