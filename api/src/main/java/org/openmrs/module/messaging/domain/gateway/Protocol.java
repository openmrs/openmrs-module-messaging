package org.openmrs.module.messaging.domain.gateway;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;

/**
 * A Protocol represents the set of rules that govern the formatting of messages
 * and addresses.
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
	
	public abstract String getProtocolAbbreviation();

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