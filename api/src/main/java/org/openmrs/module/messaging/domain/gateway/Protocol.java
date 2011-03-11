package org.openmrs.module.messaging.domain.gateway;

import java.util.Set;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.exception.AddressFormattingException;
import org.openmrs.module.messaging.domain.gateway.exception.MessageFormattingException;

/**
 * A Protocol represents the set of rules that govern the formatting of messages
 * and addresses. As such, a Protocol is a factory class for creating valid
 * messages and addresses.
 */
public abstract class Protocol {

	private MessagingAddressService addressService;
	
	protected MessagingAddressService getAddressService(){
		if(addressService == null){
			addressService = Context.getService(MessagingAddressService.class);
		}
		return addressService;
	}
	/**
	 * This method should return the display name of the protocol. This will be
	 * used in the UI and should be internationalized if necessary <br>
	 * </br> e.g. "SMS" or "Twitter"
	 */
	public abstract String getProtocolName();
	
	
	/**
	 * Creates a message. "from" can be null or blank, but will be filled in depending on which gateway sends the message. The message will also
	 * be marked as 'from' the currently authenticated user at the time of sending.
	 * 
	 * @throws MessageFormattingException
	 * @throws AddressFormattingException
	 */
	public abstract Message createMessage(String messageContent, String to, String from) throws MessageFormattingException, AddressFormattingException;
	
	/**
	 * Creates a message.  "from" can be null or blank, but will be filled in depending on which gateway sends the message. The message will also
	 * be marked as 'from' the currently authenticated user at the time of sending.
	 * 
	 * @throws MessageFormattingException
	 * @throws AddressFormattingException
	 */
	public abstract Message createMessage(String messageContent, MessagingAddress to, MessagingAddress from) throws MessageFormattingException;
	
	/**
	 * Creates a message.  "from" can be null or blank, but will be filled in depending on which gateway sends the message. The message will also
	 * be marked as 'from' the currently authenticated user at the time of sending.
	 * 
	 * @throws MessageFormattingException
	 * @throws AddressFormattingException
	 */
	public abstract Message createMessage(String messageContent, Set<MessagingAddress> to, MessagingAddress from) throws MessageFormattingException;

	/**
	 * Creates a MessagingAddress with the supplied address. 'person' can be
	 * null.
	 * 
	 * @param address
	 *            the address
	 * @param person
	 *            the person
	 * @return
	 */
	public abstract MessagingAddress createAddress(String address, Person person) throws AddressFormattingException;

	/**
	 * Returns boolean indicating whether or not the message content is valid
	 * according to this protocol
	 * 
	 * @param content
	 *            the content to validate
	 */
	public abstract boolean messageContentIsValid(String content);

	/**
	 * Returns boolean indicating whether or not the supplied address is valid
	 * according to this protocol
	 * 
	 * @param address
	 *            the address to validate
	 */
	public abstract boolean addressIsValid(String address);
}
