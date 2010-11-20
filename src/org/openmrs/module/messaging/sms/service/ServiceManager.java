package org.openmrs.module.messaging.sms.service;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.messaging.sms.service.exception.ServiceStateException;
import org.openmrs.module.messaging.web.model.ModemBean;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.Service;
import org.smslib.AGateway.Protocols;
import org.smslib.Service.ServiceStatus;
import org.smslib.modem.SerialModemGateway;

/**
 * A commandline utility for detecting connected AT devices. The detection
 * workflow was taken from ComTest in SMSLib.
 * 
 * @author Alex Anderson alex@frontlinesms.com
 * @author Dieterich Lawson
 */
public class ServiceManager {

	private static final Log log = LogFactory.getLog(ServiceManager.class);

	private ATDeviceDetector[] detectors;
	
	private List<ModemBean> detectedModems;

	public ServiceManager() {
		// if the system is a linux system, turn on serial polling
		if (System.getProperty("os.name").contains("nux")) {
			log.info("Computer is running linux. To make RXTX work properly, we must turning on serial polling");
			Service.getInstance().getSettings().SERIAL_POLLING = true;
		}
		detectedModems = new ArrayList<ModemBean>();
	}

	/**
	 * This method starts the service. It assumes that the service was stopped
	 * prior to calling this method and that there are SerialModemGateways added
	 * to the Service
	 * @throws ServiceStateException 
	 */
	private void startService() throws ServiceStateException {
		if(Service.getInstance().getServiceStatus() != ServiceStatus.STOPPED){
			throw new ServiceStateException("Tried to start a service that was not stopped");
		}
		if (Service.getInstance().getGateways().size() != 0) {
			log.info("Attempting to start service");
			try {
				Service.getInstance().startService();
				log.info("Service started successfully");
			} catch (Exception e) {
				log.error("Unable to start service", e);
			}
		}
	}
	
	public void stopService() throws ServiceStateException{
		if(Service.getInstance().getServiceStatus() != ServiceStatus.STARTED){
			throw new ServiceStateException("Tried to stop a service that was not started");
		}
		try {
			Service.getInstance().stopService();
			log.info("Service stopped successfully.");
		} catch (Exception e) {
			log.error("Error stopping Service", e);
			return;
		}
	}
	
	public void redetectModems() throws ServiceStateException {
		teardownService();
		initializeService();
	}
	
	/**
	 * This method stops the service and removes any serial modem gateays
	 * installed in the service. It assumes that the service was started before
	 * this method was called.
	 * @throws ServiceStateException 
	 */
	public void teardownService() throws ServiceStateException {
		stopService();
		List<SerialModemGateway> gatewaysToRemove = new ArrayList<SerialModemGateway>();
		for (AGateway gateway : Service.getInstance().getGateways()) {
			if (gateway instanceof SerialModemGateway) {
				gatewaysToRemove.add((SerialModemGateway) gateway);
			}
		}
		for(SerialModemGateway smg: gatewaysToRemove){
			try {
				Service.getInstance().removeGateway(smg);
			} catch (GatewayException e) {
				log.error("Error removing gateway during SmsLib Service teardown.",e);
			}
		}
	}
	
	/**
	 * Takes the service from a stopped, uninitialized state to a started, gateway-containing state
	 * @throws ServiceStateException 
	 */
	public void initializeService() throws ServiceStateException {
		if(Service.getInstance().getServiceStatus() != ServiceStatus.STOPPED){
			throw new ServiceStateException("Tried to initialize and start a service that was not stopped");
		}
		ATDeviceDetector[] detectors = detectWhileBlocking();
		addGateways(getGatewaysFromFinishedDetectors(detectors));
		startService();
	}
	
	/** Trigger detection, and return the results when it is completed. */
	public ATDeviceDetector[] detectWhileBlocking() {
		startDetection();
		waitUntilDetectionComplete(detectors);
		return getDetectors();
	}

	/** Trigger detection. */
	private void startDetection() {
		log.trace("Starting device detection...");
		Set<ATDeviceDetector> detectorsSet = new HashSet<ATDeviceDetector>();
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ATDeviceDetector d = new ATDeviceDetector(port);
				detectorsSet.add(d);
				d.start();
			} else {
				log.info("Ignoring non-serial port: " + port.getName());
			}
		}
		this.detectors = detectorsSet.toArray(new ATDeviceDetector[0]);
		log.info("All detectors started.");
	}

	/** Get the detectors. */
	public ATDeviceDetector[] getDetectors() {
		return detectors;
	}

	/** Blocks until all detectors have completed execution. */
	private void waitUntilDetectionComplete(ATDeviceDetector[] detectors) {
		boolean completed;
		do {
			completed = true;
			for (ATDeviceDetector portDetector : detectors) {
				if (!portDetector.isFinished()) {
					completed = false;
				}
			}
			DetectorUtils.sleep(500);
		} while (!completed);
	}
	
	private void addGateways(List<SerialModemGateway> gateways){		
		for (SerialModemGateway gateway: gateways) {
			try {
				Service.getInstance().addGateway(gateway);
			} catch (GatewayException e) {
				log.error("Unable to add gateway to the service",e);
			}
			gateway.setInbound(true);
			gateway.setOutbound(true);
			gateway.setProtocol(Protocols.PDU);
			gateway.getATHandler().setStorageLocations("SMMTE");
		}
	}   

	/**
	 * Prints a report detailing the devices that were detected, and creates
	 * uninitialized SerialModemGateways for each device.
	 */
	protected List<SerialModemGateway> getGatewaysFromFinishedDetectors(ATDeviceDetector[] finishedDetectors) {
		List<SerialModemGateway> gateways = new ArrayList<SerialModemGateway>();
		// All detectors are finished, so print a report
		log.info("------- Modem Detection Summary -------");
		Set<String> serials = new HashSet<String>();
		for (ATDeviceDetector d : finishedDetectors) {
			log.info("----");
			if (d.isDetected() && !d.getSerial().trim().equals("") && serials.add(d.getSerial())) {
				log.info("PORT DETECTION SUCCESSFUL  : " + d.getPortIdentifier().getName());
				log.info("SERIAL : " + d.getSerial());
				log.info("BAUD   : " + d.getMaxBaudRate());
				SerialModemGateway smg = new SerialModemGateway(d.getPortIdentifier().getName(), d.getPortIdentifier().getName(), d.getMaxBaudRate(), "", "");
				gateways.add(smg);
				
			} else {
				if (d.getSerial() == null || d.getSerial().trim().equals("")) {
					log.error("PORT DETECTION FAILED ON " + d.getPortIdentifier().getName() + " : EMPTY SERIAL");
				} else if (serials.contains(d.getSerial())) {
					log.error("PORT DETECTION FAILED ON " + d.getPortIdentifier().getName() + " : DUPLICATE SERIAL DETECTED");
				} else {
					log.error("PORT DETECTION FAILED ON  " + d.getPortIdentifier().getName());
					log.error("REASON: ", d.getException());
				}
			}
		}
		createModemBeans(gateways);
		return gateways;
	}
	
	private void createModemBeans(List<SerialModemGateway> gateways){
		detectedModems = new ArrayList<ModemBean>();
		for(SerialModemGateway gateway: gateways){
			getDetectedModemBeans().add(new ModemBean(gateway));
		}
	}
	
	public List<ModemBean> updateModemBeans(){
		if(!Service.getInstance().getServiceStatus().equals(ServiceStatus.STARTED)){
			return new ArrayList<ModemBean>();
		}
		//A list holding the modems that we want to remove because they are no longer present
		List<ModemBean> toRemove = new ArrayList<ModemBean>();
		//first, remove any gateways that are no longer there
		for(ModemBean modem: getDetectedModemBeans()){
			SerialModemGateway smg = (SerialModemGateway) Service.getInstance().getGateway(modem.getPort());
			if(smg !=null){
				modem.setStatus(smg.getStatus().toString());
			}else{
				toRemove.add(modem);
			}
		}
		//remove the extraneous modems
		for(ModemBean modem: toRemove){
			getDetectedModemBeans().remove(modem);
		}
		return getDetectedModemBeans();
	}

	/**
	 * @return the detectedModems
	 */
	public List<ModemBean> getDetectedModemBeans(){
		return detectedModems;
	}
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
