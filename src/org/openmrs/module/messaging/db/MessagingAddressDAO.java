package org.openmrs.module.messaging.db;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
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
	 * @see MessagingAddressService#getMessagingAddressesForGateway(MessagingGateway)
	 */
	public List<MessagingAddress> getMessagingAddressesForGateway(MessagingGateway gateway);

	/**
	 * @see MessagingAddressService#getMessagingAddressesForPerson(Person)
	 */
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person);

	/**
	 * @see MessagingAddressService#getMessagingAddressesForPersonAndGateway(Person, MessagingGateway)
	 */
	public List<MessagingAddress> getMessagingAddressesForPersonAndGateway(Person person, MessagingGateway gateway);

	/**
	 * @see MessagingAddressService#findMessagingAddresses(String)
	 */
	public List<MessagingAddress> findMessagingAddresses(String search);

	/**
	 * @see MessagingAddressService#findMessagingAddresses(String, MessagingGateway)
	 */
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingGateway gateway);
	
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

	public Person getPersonForAddress(String address);

	public MessagingAddress getMessagingAddress(String address);
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForClass(Class<? extends A> addressClass);
	
	public List<MessagingAddress> getMessagingAddressesForTypeName(String typeName);
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForPersonAndClass(Person person, Class<? extends A> addressClass);
	
	public List<MessagingAddress> getMessagingAddressesForPersonAndTypeName(Person person, String typeName);
}
