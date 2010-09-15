package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.schema.exception.AddressFormattingException;
import org.openmrs.module.messaging.schema.exception.MessageFormattingException;

import winterwell.jtwitter.Twitter;

/**
 * A protocol for sending Twitter messages
 * @author dieterichlawson
 *
 */
public class TwitterProtocol extends Protocol{

	public static final String PROTOCOL_NAME = "Twitter";
	public static final String PROTOCOL_ID = "twitter";
	
	private Twitter twitter;
	
	public TwitterProtocol(){
		twitter = new Twitter();
	}
	
	@Override
	public String getProtocolId() {
		return PROTOCOL_ID;
	}
	
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}
	
	/**
	 * Limits:
	 * - alphanumeric + underscores
	 * - 15 characters max, 1 character minimum
	 * 
	 * @see org.openmrs.module.messaging.schema.Protocol#addressContentIsValid(java.lang.String)
	 */
	@Override
	public boolean addressIsValid(String address) {
		return !address.matches("[^A-Za-z0-9_]") && address.length() <=15 && address.length() >0;
	}

	/**
	 * Limits:
	 * - alphanumeric + underscores
	 * - 15 chars max, 1 char min
	 * 
	 * @see org.openmrs.module.messaging.schema.Protocol#createAddress(java.lang.String, org.openmrs.Person)
	 */
	@Override
	public MessagingAddress createAddress(String address, Person person) throws AddressFormattingException {
		if(address == null){
			return null;
		}
		if(address.matches("[^A-Za-z0-9_]")){
			throw new AddressFormattingException("Username contains characters other than letters, numbers, and underscores");
		}else if(address.length() > 15){
			throw new AddressFormattingException("Username is longer than the 15 character limit");
		}else if(address.length() < 1){
			throw new AddressFormattingException("Username is blank");
		}else{
			MessagingAddress ma = new MessagingAddress(address,person);
			ma.setProtocolId(PROTOCOL_ID);
			return ma;
		}
	}

	/**
	 * Limits:
	 * - all address limits
	 * - 140 character tweet limit
	 * @see org.openmrs.module.messaging.schema.Protocol#createMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Message createMessage(String messageContent, String toAddress, String fromAddress) throws MessageFormattingException,AddressFormattingException{
		try{
			MessagingAddress to = createAddress(toAddress,null);
		}catch(AddressFormattingException e){
			AddressFormattingException f = new AddressFormattingException(e.getMessage().replace("Username", "To-address"));
			throw f;
		}
		try{
			MessagingAddress from = createAddress(fromAddress,null);
		}catch(AddressFormattingException e){
			AddressFormattingException f = new AddressFormattingException(e.getMessage().replace("Username", "From-address"));
			throw f;
		}
		
		if(!messageContentIsValid(messageContent)){
			throw new MessageFormattingException("Tweet is longer than 140 characters");
		}
		Message result = new Message(toAddress,fromAddress,messageContent);
		result.setProtocolId(this.PROTOCOL_ID);
		return result;
	}


	/**
	 * Only 140 character messages are allowed
	 * @see org.openmrs.module.messaging.schema.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean messageContentIsValid(String content) {
		return content.length() <=140;
	}
	
	/**
	 * Returns true if this user exists in the Twitter system.
	 * This method accesses the internet, and there will be a short delay or
	 * failures due to twitter connectivity
	 * @param username
	 * @return Whether or not this user exists
	 */
	public boolean usernameExists(String username){
		return twitter.userExists(username);
	}
	
	/**
	 * Returns true if the supplied username and password can
	 * be used to log in to Twitter. This method accesses the internet,
	 * so there will be a short delay. Additionally, failures are possible
	 * if Twitter is down or something else is not working.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean isValidLogin(String username, String password){
		twitter = new Twitter(username, password);
		boolean result = twitter.isValidLogin();
		twitter = new Twitter();
		return result;
	}

	@Override
	public boolean requiresPassword() {
		return true;
	}

}
