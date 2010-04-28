package org.openmrs.module.messaging.sms;

public class ModemInfo {

	private String number;
	private String port;
	private String model;
	private String status;
	private String manufacturer;
	private String gatewayId;
	
	
	public ModemInfo(String number, String port, String model, String manufacturer, String status, String gatewayId) {
		super();
		this.number = number;
		this.port = port;
		this.model = model;
		this.status = status;
		this.manufacturer = manufacturer;
		this.setGatewayId(gatewayId);
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getGatewayId() {
		return gatewayId;
	}
}
