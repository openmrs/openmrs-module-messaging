package org.openmrs.module.messaging.web.model;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.GatewayException;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;


public class ModemBean {
	
	private static final Log log = LogFactory.getLog(ModemBean.class);
	private String port;
	private String number;
	private String status;
	private String model;
	private String serial;
	
	public ModemBean(String port, String number, String status){
		this.port = port;
		this.number = number;
		this.status = status;
	}
	
	public ModemBean(SerialModemGateway gateway){
		this.port = gateway.getGatewayId();
		this.number = gateway.getFrom();
		this.status = gateway.getStatus().toString();
		try {
			this.model = gateway.getManufacturer() + " "+ gateway.getModel();
		} catch (Exception e) {
			log.error("Error getting manufacturer and model information from modem");
		} 
		try {
			this.serial = gateway.getSerialNo();
		} catch (Exception e) {
			log.error("Error getting serial number from modem");
		}
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}
