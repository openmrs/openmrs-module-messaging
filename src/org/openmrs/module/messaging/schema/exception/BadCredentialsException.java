package org.openmrs.module.messaging.schema.exception;

@SuppressWarnings("serial")
public class BadCredentialsException extends Exception{
	public BadCredentialsException(String message){
		super(message);
	}
}
