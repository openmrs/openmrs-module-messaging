package org.openmrs.module.messaging.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.EncryptionService;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.springframework.util.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptionServiceImpl implements EncryptionService {

	/**
	 * encryption settings
	 */
	private static final String CIPHER_CONFIGURATION = "AES/CBC/PKCS5Padding";
	private static final String KEY_SPEC = "AES";

	private Cipher encryptionCipher = null;
	private Cipher decryptionCipher = null;
	private SecretKeySpec secret;
	private IvParameterSpec initVector;

	/**
	 * default blank constructor
	 */
	public EncryptionServiceImpl() {
		super();
	}
	
	/**
	 * convenience constructor to provide a custom encryption service 
	 * @param iv
	 * @param key
	 */
	public EncryptionServiceImpl(byte[] iv, byte[] key) {
		super();
		setInitVector(new IvParameterSpec(iv));
		setSecret(new SecretKeySpec(key, KEY_SPEC));
	}
	
	public void onStartup() {
		// TODO Auto-generated method stub
	}

	public void onShutdown() {
		// TODO Auto-generated method stub
	}

	/**
	 * encrypt text to a string
	 * 
	 * @param text
	 * @return encrypted text
	 * @throws APIException 
	 */
	public String encrypt(String text) throws APIException {
		byte[] encrypted;

		try {
			Cipher cipher = this.getEncryptionCipher();
			encrypted = cipher.doFinal(text.getBytes());
		} catch (GeneralSecurityException e) {
			throw new APIException("could not encrypt text", e);
		} catch (IOException e) {
			throw new APIException("could not encrypt text", e);
		}
		
		return new BASE64Encoder().encode(encrypted);
	}

	/**
	 * decrypt text from a string
	 * 
	 * @param text
	 * @return decrypted text
	 * @should decrypt text
	 */
	public String decrypt(String text) throws APIException {
		String decrypted = null;

		try {
			Cipher cipher = this.getDecryptionCipher();
			byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(text));
			decrypted = new String(original);
		} catch (GeneralSecurityException e) {
			throw new APIException("could not decrypt text", e);
		} catch (IOException e) {
			throw new APIException("could not decrypt text", e);
		}

		return decrypted;
	}

	/**
	 * generates a new encryption cipher if it does not already exist
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException 
	 */
	private Cipher getEncryptionCipher() throws GeneralSecurityException, IOException {
		if (encryptionCipher == null) {
			encryptionCipher = Cipher.getInstance(CIPHER_CONFIGURATION);
			encryptionCipher.init(Cipher.ENCRYPT_MODE, getSecret(), getInitVector());
		}
		return encryptionCipher;
	}

	/**
	 * generates a new decryption cipher if it does not already exist
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException 
	 */
	private Cipher getDecryptionCipher() throws GeneralSecurityException, IOException {
		if (decryptionCipher == null) {
			decryptionCipher = Cipher.getInstance(CIPHER_CONFIGURATION);
			decryptionCipher.init(Cipher.DECRYPT_MODE, getSecret(), getInitVector());
		}
		return decryptionCipher;
	}
	
	/**
	 * returns an init vector for creating a decryption cipher
	 * 
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private byte[] getSavedInitVector() throws IOException {
		String initVectorText = Context.getAdministrationService()
				.getGlobalProperty(MessagingConstants.GP_ENCRYPTION_VECTOR);
		
		if (StringUtils.hasText(initVectorText))
			return new BASE64Decoder().decodeBuffer(initVectorText);

		byte[] iv = generateNewInitVector();

		// save the new init vector
		GlobalProperty gp = Context.getAdministrationService()
				.getGlobalPropertyObject(MessagingConstants.GP_ENCRYPTION_VECTOR);

		if (gp == null) {
			// global property was not initialized
			gp = new GlobalProperty();
			gp.setProperty(MessagingConstants.GP_ENCRYPTION_VECTOR);
		}
		
		gp.setPropertyValue(new BASE64Encoder().encode(iv));
		Context.getAdministrationService().saveGlobalProperty(gp);
		
		return iv;
	}
	
	private byte[] generateNewInitVector() {
		// initialize the init vector with 16 random bytes
		byte[] initVector = new byte[16];
		new SecureRandom().nextBytes(initVector);

		// TODO get the following (better) method working
//		Cipher cipher = Cipher.getInstance(CIPHER_CONFIGURATION);
//		AlgorithmParameters params = cipher.getParameters();
//
//		if (params == null)
//			throw new APIException("could not generate init vector, null params");
//		
//		byte[] initVector = params.getParameterSpec(IvParameterSpec.class).getIV();

		return initVector;
	}
	
	/**
	 * retrieve the secret key from global properties and generate it if needed
	 */
	private byte[] getSavedSecretKey() throws IOException {
		String keyText = Context.getAdministrationService()
				.getGlobalProperty(MessagingConstants.GP_ENCRYPTION_KEY);

		if (StringUtils.hasText(keyText))
			return new BASE64Decoder().decodeBuffer(keyText);

		// generate and save the key
		byte[] key = generateNewSecretKey();
		GlobalProperty gp = Context.getAdministrationService()
				.getGlobalPropertyObject(MessagingConstants.GP_ENCRYPTION_KEY);

		if (gp == null) {
			// global property was not initialized
			gp = new GlobalProperty();
			gp.setProperty(MessagingConstants.GP_ENCRYPTION_KEY);
		}
		
		gp.setPropertyValue(new BASE64Encoder().encode(key));
		Context.getAdministrationService().saveGlobalProperty(gp);
		
		return key;
	}

	/**
	 * generate a new secret key; should only be called once in order to not invalidate all encrypted data
	 * 
	 * @return
	 */
	private byte[] generateNewSecretKey() {
		// Get the KeyGenerator
		KeyGenerator kgen = null;
		try {
			kgen = KeyGenerator.getInstance(KEY_SPEC);
		} catch (NoSuchAlgorithmException e) {
			throw new APIException("Could not generate cipher key", e);
		}
		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		SecretKey skey = kgen.generateKey();
		
		return skey.getEncoded();
	}
	
	/**
	 * @return the initVector
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	private IvParameterSpec getInitVector() throws IOException {
		if (initVector == null)
			setInitVector(new IvParameterSpec(getSavedInitVector()));
		return initVector;
	}

	/**
	 * @param initVector the initVector to set
	 */
	private void setInitVector(IvParameterSpec initVector) {
		this.initVector = initVector;
	}

	/**
	 * @return the secret
	 * @throws IOException 
	 */
	private SecretKeySpec getSecret() throws IOException {
		if (secret == null)
			setSecret(new SecretKeySpec(getSavedSecretKey(), KEY_SPEC));
		return this.secret;
	}

	/**
	 * @param secret the secret to set
	 */
	private void setSecret(SecretKeySpec secret) {
		this.secret = secret;
	}

}
