package org.openmrs.module.messaging.schema;

/**
 * The exception thrown when an attempt is made to create an 
 * incorrectly formatted Address.
 */
public class AddressFormattingException extends Exception {

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
