package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.springframework.transaction.annotation.Transactional;

public interface MessagingAddressService extends OpenmrsService{

	/**
	 * @return All messaging addresses
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getAllMessagingAddresses();

	/**
	 * @param addressId
	 *            the addressId of the MessagingAddress that you want
	 * @return the MessagingAddress with an addressId that matches the parameter
	 *         you supplied
	 */
	@Transactional(readOnly=true)
	public MessagingAddress getMessagingAddress(Integer addressId);

	/**
	 * Returns all messages that the supplied service is able to send. This is
	 * determined using the parameter M in MessagingService<M extends Message, A
	 * extends MessagingAddress>
	 * 
	 * @param service
	 *            The service
	 * @return the messages for that service
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getMessagingAddressesForService(MessagingService service);

	/**
	 * @param person
	 *            The person
	 * @return all messaging addresses for that person
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person);

	/**
	 * @param person
	 *            The person
	 * @param service
	 *            The service
	 * @return all addresses for the person that the service can send to/from
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getMessagingAddressesForPersonAndService(Person person, MessagingService service);

	/**
	 * This method performs a like query on MessagingAddress.address with
	 * wildcards before and after the search string
	 * 
	 * @param search The string to search for
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> findMessagingAddresses(String search);

	/**
	 * This method performs a like query on MessagingAddress.address with
	 * wildcards before and after the search string
	 * 
	 * @param search The string to search for
	 * @param service The service that handles the desired message type 
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingService service);
	
	/**
	 * Gets the preferred messaging address of the person
	 * @param person
	 * @return
	 */
	@Transactional(readOnly=true)
	public MessagingAddress getPreferredMessagingAddressForPerson(Person person);
	
	/**
	 * Saves a MessagingAddress
	 * @param address
	 */
	@Transactional
	public void saveMessagingAddress(MessagingAddress address);

	/**
	 * Deletes a MessagingAddress
	 * @param address
	 */
	@Transactional
	public void deleteMessagingAddress(MessagingAddress address);

	/**
	 * Retires a Messaging address with the supplied reason
	 * @param address
	 * @param reason
	 */
	@Transactional
	public void retireMessagingAddress(MessagingAddress address, String reason);

	/**
	 * Unretires a MessagingAddress, leaving retiredBy, dateRetired, and retireReason set
	 * as markers of the last time the object was retired
	 * @param address
	 */
	@Transactional
	public void unretireMessagingAddress(MessagingAddress address);
	
}
