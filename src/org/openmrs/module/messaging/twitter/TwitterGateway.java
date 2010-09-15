package org.openmrs.module.messaging.twitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.CredentialSet;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;

import winterwell.jtwitter.Twitter;

public class TwitterGateway extends MessagingGateway {

	public static final String GATEWAY_ID = "twitter";
	public static final String GATEWAY_NAME = "Twitter";
	
	private static Log log = LogFactory.getLog(TwitterGateway.class);
	
	/**
	 * Object that encapsulates a session with the Twitter API
	 */
	private Twitter twitter;
	
	/**
	 * The in-use login credentials
	 */
	private CredentialSet  currentCredentials;
	
	@Override
	public String getName() {
		return GATEWAY_NAME;
	}
	
	@Override
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("messaging.twitter.gateway.description");
	}
	
	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canSend() {
		return true;
	}
	
	/**
	 * Returns a pair of strings. The first string is the username
	 * and the second string is the password
	 * @return
	 */
	private CredentialSet getCredentials(){
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME);
		String password= Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD);
		Context.closeSession();
		CredentialSet credentials = new CredentialSet(username,password);
		return credentials;
	}
	
	public void updateCredentials(String username, String password){
		this.currentCredentials = new CredentialSet(username, password);
		this.twitter = new Twitter(username,password);
	}
	

	@Override
	public void shutdown() {
		twitter = null;
	}

	@Override
	public void startup() {
		currentCredentials = getCredentials();
		twitter = new Twitter(currentCredentials.getUsername(),currentCredentials.getPassword());
	}

	@Override
	public boolean supportsProtocol(Protocol protocol) {
		return protocol.getProtocolId().equals(TwitterProtocol.PROTOCOL_ID);
	}

	@Override
	public boolean isActive() {
		try{
			twitter.getStatus();
		}catch(Throwable t){
			return false;
		}
		return true;
	}

	@Override
	public void sendMessage(Message message) {
		twitter.sendMessage(message.getDestination(), message.getContent());
	}

	@Override
	public boolean shouldSendMessage(Message m) {
		return false;
	}

}
