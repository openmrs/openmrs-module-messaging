package org.openmrs.module.messaging.schema;

public class MessageFormattingException extends Exception {

	private String description;

	public MessageFormattingException(String description) {
		super();
		this.description = description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
