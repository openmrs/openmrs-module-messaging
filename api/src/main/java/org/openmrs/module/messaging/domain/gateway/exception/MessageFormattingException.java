package org.openmrs.module.messaging.domain.gateway.exception;

@SuppressWarnings("serial")
public class MessageFormattingException extends Exception {
	public MessageFormattingException(String message){
		super(message);
	}
}
