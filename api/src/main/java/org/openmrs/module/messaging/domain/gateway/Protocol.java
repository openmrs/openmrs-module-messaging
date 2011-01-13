package org.openmrs.module.messaging.domain.gateway;

import org.openmrs.Person;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.exception.AddressFormattingException;
import org.openmrs.module.messaging.domain.gateway.exception.MessageFormattingException;

/**
 * A Protocol represents the set of rules that govern the formatting of messages
 * and addresses. As such, a Protocol is a factory class for creating valid
 * messages and addresses.
 * 
 */
public abstract class Protocol {

	/**
	 * This method should return the display name of the protocol. This will be
	 * used in the UI and should be internationalized if necessary <br>
	 * </br> e.g. "SMS" or "Twitter"
	 */
	public abstract String getProtocolName();

	/**
	 * Creates a message using the supplied parameters. Any of them can be null,
	 * but that is not suggested because the point of this method is to validate
	 * the parameters. <br>
	 * </br> The parameters to this method are the only {@link Message} fields
	 * that require validation - after creating a message with this method, feel
	 * free to modify other message fields like {@link Message#sender} and
	 * {@link Message#recipient} as you wish.
	 * 
	 * @param messageContent
	 *            the text content
	 * @param toAddress
	 *            the destination address
	 * @param fromAddress
	 *            the originating address
	 * @return
	 */
	public abstract Message createMessage(String messageContent,
			String toAddress, String fromAddress)
			throws MessageFormattingException, AddressFormattingException;

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
	public abstract MessagingAddress createAddress(String address, Person person)
			throws AddressFormattingException;

	/**
	 * Returns boolean indicating whether or not the message content is valid
	 * according to this protocol
	 * 
	 * @param content
	 *            the content to validate
	 * @return
	 */
	public abstract boolean messageContentIsValid(String content);

	/**
	 * Returns boolean indicating whether or not the supplied address is valid
	 * according to this protocol
	 * 
	 * @param address
	 *            the address to validate
	 * @return
	 */
	public abstract boolean addressIsValid(String address);

	public abstract boolean requiresPassword();
	
	public String getProtocolId(){
		return getClass().getName();
	}

}
