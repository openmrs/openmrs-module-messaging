package org.openmrs.module.messaging.schema;

/**
 * A class that represents a set of credentials
 */
public class CredentialSet {
	
	private String username;
	private String password;
	
	public CredentialSet(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
}
