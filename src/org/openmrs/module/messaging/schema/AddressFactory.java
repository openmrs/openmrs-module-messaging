package org.openmrs.module.messaging.schema;

/**
 * An interface that classes responsible for formatting and creation of
 * messaging addresses should implement.
 * 
 * @param <A>
 *            The type of address that this factory creates.
 * 
 * @see MessageAddress
 */
public interface AddressFactory<A extends MessageAddress> {

	/**
	 * Creates a MessageAddress using the provided string. If the address is
	 * improperly formatted, an exception is thrown.
	 * 
	 * @param address
	 * @return an Address object
	 * @throws AddressFormattingException
	 */
	public A createAddress(String address) throws AddressFormattingException;

	/**
	 * Returns a boolean representing whether or not the string address is valid.
	 * 
	 * @param address The address to evaluate
	 * @return
	 */
	public Boolean addressIsValid(String address);
}
