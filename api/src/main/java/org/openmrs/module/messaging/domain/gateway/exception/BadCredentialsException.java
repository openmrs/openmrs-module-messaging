package org.openmrs.module.messaging.domain.gateway.exception;

@SuppressWarnings("serial")
public class BadCredentialsException extends Exception{
	public BadCredentialsException(String message){
		super(message);
	}
}
