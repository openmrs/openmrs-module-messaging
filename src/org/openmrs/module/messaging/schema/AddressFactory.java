package org.openmrs.module.messaging.schema;

import org.openmrs.Person;

/**
 * An interface that classes responsible for formatting and creation of
 * messaging addresses should implement.
 * 
 * @param <A>
 *            The type of address that this factory creates.
 * 
 * @see MessagingAddress
 */
public interface AddressFactory<A extends MessagingAddress> {

	/**
	 * Creates a MessagingAddress using the provided string. If the address is
	 * improperly formatted, an exception is thrown.
	 * 
	 * @param address
	 * @param person
	 * @return an Address object
	 * @throws AddressFormattingException
	 */
	public A createAddress(String address, Person person)
			throws AddressFormattingException;

	/**
	 * Returns a boolean representing whether or not the string address is
	 * valid.
	 * 
	 * @param address
	 *            The address to evaluate
	 * @return
	 */
	public Boolean addressIsValid(String address);

	/**
	 * This method should return a short string that can clue users as to the
	 * formatting of the address.
	 * 
	 * E.g. "###.###.#####" or "ex: johnDoe@some.com"
	 * 
	 * @return
	 */
	public String getFormatHint();
}
