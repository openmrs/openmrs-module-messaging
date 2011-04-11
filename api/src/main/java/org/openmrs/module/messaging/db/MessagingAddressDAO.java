package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public interface MessagingAddressDAO {
	
	/**
	 * @see MessagingAddressService#getAllMessagingAddresses()
	 */
	public List<MessagingAddress> getAllMessagingAddresses();

	/**
	 * @see MessagingAddressService#getMessagingAddress(Integer)
	 */
	public MessagingAddress getMessagingAddress(Integer addressId);

	/**
	 * @see MessagingAddressService#getPreferredMessagingAddressForPerson(Person)
	 */
	public MessagingAddress getPreferredMessagingAddressForPerson(Person person);
	
	/**
	 * @see MessagingAddressService#saveMessagingAddress(MessagingAddress)
	 */
	public void saveMessagingAddress(MessagingAddress address);

	/**
	 * @see MessagingAddressService#deleteMessagingAddress(MessagingAddress)
	 */
	public void deleteMessagingAddress(MessagingAddress address);

	/**
	 * @see MessagingAddressService#voidMessagingAddress(MessagingAddress, String)
	 */
	public void voidMessagingAddress(MessagingAddress address, String reason);

	/**
	 * @see MessagingAddressService#unvoidMessagingAddress(MessagingAddress)
	 */
	public void unvoidMessagingAddress(MessagingAddress address);

	public Person getPersonForAddress(String address);

	public MessagingAddress getMessagingAddress(String address);

	public List<MessagingAddress> findMessagingAddresses(String address, Class<? extends Protocol> protocolClass, Person person, boolean includeVoided);

	public List<MessagingAddress> getPublicAddressesForPerson(Person p);
}
