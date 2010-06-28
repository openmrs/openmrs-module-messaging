package org.openmrs.module.messaging.schema;

import org.openmrs.Person;

public abstract class Protocol {

	/**
	 * should return a string ID of the protocol <br>
	 * </br> e.g. "sms" or "twitter"
	 */
	public abstract String getProtocolId();

	/**
	 * @return The display name of the protocol. This will be used in the UI and
	 *         should be internationalized if necessary
	 */
	public abstract String getProtocolName();

	/**
	 * Creates a message using the supplied parameters. Any of them can be null,
	 * but that is not suggested because the point of this method is to validate
	 * the parameters.
	 * 
	 * @param messageContent
	 * @return
	 */
	public abstract Message createMessage(String messageContent, String toAddress, String fromAddress) throws MessageFormattingException,AddressFormattingException;

	
	/**
	 * Creates a message using the supplied parameters. This method is useful
	 * if you already have MessagingAddress objects created, or if there are weird
	 * issues with the default validation mechanisms. 
	 * 
	 * @param messageContent
	 * @return
	 */
	public abstract Message createMessage(String messageContent) throws MessageFormattingException;
	
	/**
	 * Creates a MessagingAddress with the supplied address.
	 * 'person' can be null
	 * @param address
	 * @return
	 */
	public abstract MessagingAddress createAddress(String address, Person person) throws AddressFormattingException;

	/**
	 * Returns boolean indicating whether or not the message content is valid for this protocol
	 * @param content
	 * @return
	 */
	public abstract boolean messageContentIsValid(String content);

	/**
	 * Returns boolean indicating whether or not the supplied address is valid for this protocol
	 * @param content
	 * @return
	 */
	public abstract boolean addressIsValid(String address);

}
