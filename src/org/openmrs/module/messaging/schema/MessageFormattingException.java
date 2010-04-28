package org.openmrs.module.messaging.schema;

@SuppressWarnings("serial")
public class MessageFormattingException extends Exception {

	/**
	 * This description will be displayed to the user when
	 * this exception is thrown.
	 */
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
