package org.openmrs.module.messaging.sms.servicemanager.exception;

@SuppressWarnings("serial")
public class ServiceStateException extends Exception {
	public ServiceStateException(String message){
		super(message);
	}
}
