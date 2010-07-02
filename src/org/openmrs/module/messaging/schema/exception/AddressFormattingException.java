package org.openmrs.module.messaging.schema.exception;

/**
 * The exception thrown when an attempt is made to create an 
 * incorrectly formatted Address.
 */
@SuppressWarnings("serial")
public class AddressFormattingException extends Exception {

	/**
	 * This description will be displayed to the user when 
	 * this exception is thrown 
	 */
	private String description;

	public void setDescription(String description) {
		this.description = description;
	}

	public AddressFormattingException(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
