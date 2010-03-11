package org.openmrs.module.messaging.framework;

/**
 * An interface that classes responsible for formatting and creation of
 * addresses should implement.
 * 
 * @param <A>
 *            The type of address that this factory creates. This could
 *            theoretically just be {@link Address} if you are using the
 *            standard address type
 */
public interface AddressFactory<A extends Address> {

	/**
	 * Creates an Address using the provided string. If the address is
	 * improperly formatted, an exception is thrown.
	 * 
	 * @param address
	 * @return an Address object
	 * @throws AddressFormattingException
	 */
	public A createAddress(String address) throws AddressFormattingException;

	/**
	 * Creates an Address using the provided address and password. If the
	 * address is improperly formatted, an exception is thrown.
	 * 
	 * @param address
	 * @param password
	 * @return an Address object
	 * @throws AddressFormattingException
	 */
	public A createAddress(String address, String password)
			throws AddressFormattingException;

	/**
	 * Returns a boolean representing whether or not the address is valid.
	 * 
	 * @param address
	 *            The address to evaluate
	 * @return
	 */
	public Boolean isAddressValid(A address);
}
