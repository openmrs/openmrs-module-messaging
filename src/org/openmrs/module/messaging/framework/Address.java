package org.openmrs.module.messaging.framework;

/**
 * Address objects are basically wrappers around a string that represents a
 * point where a message is sent or a point that a message is sent from. All
 * addresses must be representable using less than 255 characters of plain-text
 * ('address' is a varchar(255) in the database). </br></br>When creating your
 * own messaging services, you can extend Address to create your own address
 * types or you can just use this class as is, but if you extend Address you
 * cannot add any persistent fields because they will not be stored in the
 * database.</br> </br> Some addresses (like twitter usernames) require
 * passwords to be able to send messages from them. Addresses which do not
 * require passwords do not need to mess with those fields. </br</br> Creating
 * Address objects should be done using a class that implements the
 * {@link AddressFactory} interface, which is responsible for making sure that
 * addresses are properly formatted. Any addresses created outside of the
 * address factory are not guaranteed to be valid.</br></br>Lastly, the only
 * address objects that are stored in the database are addresses of people in
 * the system. Those addresses are pointed to via a foreign-keyed
 * {@link PersonAttribute}.
 * 
 */
public class Address {

	/** The messaging address */
	private String address;

	/** The password to access that address, if required */
	private String password;

	/**
	 * boolean indicating whether or not a password is required to access the
	 * address
	 */
	private Boolean passwordIsRequired;

	/**
	 * A boolean indicating whether this is the owner's preferred messaging
	 * address
	 */
	private Boolean isPreferredMessagingAddress;

	/**
	 * Creates an Address object. Sets isPrefferedMessagingAddress to false
	 * 
	 * @param address
	 */
	public Address(String address) {
		this.setAddress(address);
		this.setPassword("");
		this.setPasswordIsRequired(false);
		this.setIsPreferredMessagingAddress(false);
	}

	/**
	 * Creates an address with an address and password and then sets
	 * isPrefferedMessagingAddress to false
	 * 
	 * @param address
	 * @param password
	 */
	public Address(String address, String password) {
		this.setAddress(address);
		this.setPassword(password);
		this.setPasswordIsRequired(true);
		this.setIsPreferredMessagingAddress(false);
	}

	/** @param isPreferredMessagingAddress */
	public void setIsPreferredMessagingAddress(
			Boolean isPreferredMessagingAddress) {
		this.isPreferredMessagingAddress = isPreferredMessagingAddress;
	}

	/** @return whether or not this is the preferred messaging address */
	public Boolean isPreferredMessagingAddress() {
		return isPreferredMessagingAddress;
	}

	/**
	 * @param passwordIsRequired
	 *            the passwordIsRequired to set
	 */
	public void setPasswordIsRequired(Boolean passwordIsRequired) {
		this.passwordIsRequired = passwordIsRequired;
	}

	/** @return whether or not a password is required to access this address */
	public Boolean getPasswordIsRequired() {
		return passwordIsRequired;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return the password */
	public String getPassword() {
		return password;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/** @return the address */
	public String getAddress() {
		return address;
	}
}
