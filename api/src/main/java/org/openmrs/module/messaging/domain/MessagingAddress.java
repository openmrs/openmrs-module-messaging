package org.openmrs.module.messaging.domain;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Person;
import org.openmrs.module.messaging.domain.gateway.Protocol;

/**
 * MessagingAddress objects represent a 'place' that a message is sent to or from.
 * All addresses must be representable using less than 255 characters of
 * plain-text ('address' is a varchar(255) in the database). <br>
 * <br/>
 * Creating MessagingAddress objects should be done using a class that extends
 * {@link Protocol}. Any addresses created outside the Protocol factory methods
 * are not guaranteed to be valid.
 * 
 * @see Protocol
 */
public class MessagingAddress extends BaseOpenmrsData {

	public MessagingAddress() {}
	
	public MessagingAddress(String address, Person person) {
		super();
		this.address = address;
		this.person = person;
	}
 
	private Integer messagingAddressId;

	/**
	 * The plain text address
	 */
	protected String address;

	/**
	 * The person this address is for
	 */
	protected Person person;

	/**
	 * Boolean that represents whether or not this is the preferred method of
	 * contact for a patient
	 */
	protected Boolean preferred = false;

	/**
	 * Represents whether or not this message can be found in the OpenMRS
	 * address directory (i.e. other users can find this address and send
	 * messages to it)
	 */
	private boolean findable;
	
	/**
	 * The protocol that is used to send messages to this address
	 */
	protected String protocolClass;


	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	public Integer getId() {
		return getMessagingAddressId();
	}

	public void setId(Integer id) {
		this.setMessagingAddressId(id);
	}

	/**
	 * @param preferred
	 *            the preferred to set
	 */
	public void setPreferred(Boolean preferred) {
		this.preferred = preferred;
	}

	/**
	 * @return the preferred
	 */
	public Boolean getPreferred() {
		return preferred;
	}

	public boolean equals(MessagingAddress other) {
		return this.getMessagingAddressId() == other.getId();
	}

	/**
	 * @param protocolId
	 *            the protocolId to set
	 */
	public void setProtocol(Class<? extends Protocol> protocolClass) {
		this.protocolClass = protocolClass.getName();
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Protocol> getProtocol() {
		try {
			return (Class<? extends Protocol>) Class.forName(protocolClass);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * @param messagingAddressId
	 *            the messagingAddressId to set
	 */
	public void setMessagingAddressId(Integer messagingAddressId) {
		this.messagingAddressId = messagingAddressId;
	}

	/**
	 * @return the messagingAddressId
	 */
	public Integer getMessagingAddressId() {
		return messagingAddressId;
	}

	/**
	 * @param findable
	 *            the findable to set
	 */
	public void setFindable(boolean findable) {
		this.findable = findable;
	}

	/**
	 * @return the findable
	 */
	public boolean isFindable() {
		return findable;
	}
}
