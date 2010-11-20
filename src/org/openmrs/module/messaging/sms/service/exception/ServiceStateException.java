package org.openmrs.module.messaging.sms.service.exception;

@SuppressWarnings("serial")
public class ServiceStateException extends Exception {
	public ServiceStateException(String message){
		super(message);
	}
}
