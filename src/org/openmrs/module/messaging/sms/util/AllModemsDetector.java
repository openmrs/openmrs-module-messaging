package org.openmrs.module.messaging.sms.util;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.Service;
import org.smslib.AGateway.Protocols;
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
	
	protected static final Log log = LogFactory.getLog(AllModemsDetector.class);
	
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
			DetectorUtils.sleep(500);
		} while(!completed);
	}
	
	/** Prints a report to {@link System#out} detailing the devices that were detected. */
	protected static Service detectModems(ATDeviceDetector[] completedDetectors) {
		// All detectors are finished, so print a report
		Service s = new Service();
		log.info("------- Beginning modem detection -------");
		//if the system is a linux system, turn on serial polling
		if(System.getProperty("os.name").contains("nux")){
			log.info("Messaging: computer is running linux. To make RXTX work properly, we must turning on serial polling");
			s.getSettings().SERIAL_POLLING=true;
		}
		for(ATDeviceDetector d : completedDetectors) {
			log.info("----");
			log.info("PORT   : " + d.getPortIdentifier().getName());
			if(d.isDetected()) {
				log.info("SERIAL : " + d.getSerial());
				log.info("BAUD   : " + d.getMaxBaudRate());
				SerialModemGateway smg = new SerialModemGateway(d.getPortIdentifier().getName(),d.getPortIdentifier().getName(), d.getMaxBaudRate(), "", "");
				//detect model
				
				try {
					s.addGateway(smg);
				} catch (Exception e) {
					log.info("Unable to add modem gateway to service\n"+e);
				}
				smg.setInbound(true);
				smg.setOutbound(true);
				smg.setProtocol(Protocols.TEXT);
			} else {
				log.error("DETECTION FAILED " + d.getException().getStackTrace());
			}
		}
		if(s.getGateways().size() !=0){
			log.info("Attempting to start service");
			try {
				s.startService();
				log.info("Service started successfully");
			} catch (Exception e) {
				log.error("Unable to start service", e);
			}
		}
		return s;
	}
}

