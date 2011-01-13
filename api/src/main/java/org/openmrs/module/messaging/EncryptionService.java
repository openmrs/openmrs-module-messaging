package org.openmrs.module.messaging;

import org.openmrs.api.OpenmrsService;

public interface EncryptionService extends OpenmrsService {

	/**
	 * encrypts text
	 * 
	 * @param text
	 * @return
	 * @should encrypt small and large text
	 */
	public String encrypt(String text);
	
	/**
	 * decrypts text
	 * 
	 * @param text
	 * @return
	 * @should decrypt small and large text
	 */
	public String decrypt(String text);
	
}
