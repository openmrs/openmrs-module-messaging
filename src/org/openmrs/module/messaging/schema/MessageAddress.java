package org.openmrs.module.messaging.schema;

import java.util.List;

import org.openmrs.Attributable;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.PersonAttribute;

/**
 * MessageAddress objects are wrappers around a string that represents a place
 * that a message is sent to or from. All addresses must be representable using
 * less than 255 characters of plain-text ('address' is a varchar(255) in the
 * database). </br></br>When creating your own messaging services, you can
 * extend MessageAddress to create your own address types or you can just use
 * this class as is.</br> </br> Creating MessageAddress objects
 * should be done using a class that implements the {@link AddressFactory}
 * interface, which is responsible for making sure that addresses are properly
 * formatted. Any addresses created outside of the address factory are not
 * guaranteed to be valid.</br></br> Messaging Addresses are stored as
 * foreign-keyed person attributes, and the only address objects that are stored
 * in the database are addresses of people in the system.
 * 
 * @see PersonAttribute
 * @see AddressFactory
 */
public class MessageAddress extends BaseOpenmrsMetadata implements
		Attributable<MessageAddress> {

	protected Integer messagingAdressId;

	protected Boolean retired = false;

	/**
	 * The messaging address
	 */
	private String address;
	
	private String password;

	/**
	 * Creates a MessageAddress object.
	 * 
	 * @param address
	 */
	public MessageAddress(String address) {
		this.setAddress(address);
	}

	public MessageAddress() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	public String serialize() {
		if (getId() == null)
			return "";
		else
			return "" + getId();
	}

	public Integer getId() {
		return messagingAdressId;
	}

	public void setId(Integer id) {
		this.messagingAdressId = id;
	}

	public List<MessageAddress> findPossibleValues(String searchText) {
		return null;
	}

	public List<MessageAddress> getPossibleValues() {
		return null;
	}

	public MessageAddress hydrate(String s) {
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
}
