package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
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
	 * @param address
	 * @return
	 */
	@Transactional(readOnly=true)
	public MessagingAddress getMessagingAddress(String address);

	/**
	 * Returns all messages that the supplied gateway is able to send. This is
	 * determined using the parameter M in MessagingGateway<M extends Message, A
	 * extends MessagingAddress>
	 * 
	 * @param gateway
	 *            The gateway
	 * @return the messages for that gateway
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getMessagingAddressesForGateway(MessagingGateway gateway);

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
	 * @param gateway
	 *            The gateway
	 * @return all addresses for the person that the gateway can send to/from
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> getMessagingAddressesForPersonAndGateway(Person person, MessagingGateway gateway);

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
	 * @param gateway The gateway that handles the desired message type 
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingGateway gateway);
	
	/**
	 * Gets the preferred messaging address of the person
	 * @param person
	 * @return
	 */
	@Transactional(readOnly=true)
	public MessagingAddress getPreferredMessagingAddressForPerson(Person person);
	
	@Transactional(readOnly=true)
	public Person getPersonForAddress(String address);
	
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
