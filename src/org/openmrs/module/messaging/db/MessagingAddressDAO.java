package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

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
	 * @see MessagingAddressService#getMessagingAddressesForService(MessagingService)
	 */
	public List<MessagingAddress> getMessagingAddressesForService(MessagingService service);

	/**
	 * @see MessagingAddressService#getMessagingAddressesForPerson(Person)
	 */
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person);

	/**
	 * @see MessagingAddressService#getMessagingAddressesForPersonAndService(Person, MessagingService)
	 */
	public List<MessagingAddress> getMessagingAddressesForPersonAndService(Person person, MessagingService service);

	/**
	 * @see MessagingAddressService#findMessagingAddresses(String)
	 */
	public List<MessagingAddress> findMessagingAddresses(String search);

	/**
	 * @see MessagingAddressService#findMessagingAddresses(String, MessagingService)
	 */
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingService service);
	
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
	 * @see MessagingAddressService#retireMessagingAddress(MessagingAddress, String)
	 */
	public void voidMessagingAddress(MessagingAddress address, String reason);

	/**
	 * @see MessagingAddressService#unretireMessagingAddress(MessagingAddress)
	 */
	public void unvoidMessagingAddress(MessagingAddress address);

}
