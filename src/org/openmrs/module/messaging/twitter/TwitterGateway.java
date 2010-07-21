package org.openmrs.module.messaging.twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.CredentialSet;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;

import com.techventus.server.voice.Voice;

import sun.util.logging.resources.logging;
import winterwell.jtwitter.Twitter;

public class TwitterGateway extends MessagingGateway {

	public static final String GATEWAY_ID = "twitter";
	public static final String GATEWAY_NAME = "Twitter";
	
	private static Log log= LogFactory.getLog(TwitterGateway.class);
	
	/**
	 * Object that encapsulates a session with the Twitter API
	 */
	private Twitter twitter;
	
	/**
	 * The in-use login credentials
	 */
	private CredentialSet  currentCredentials;
	
	/**
	 * All the from-addresses that this gateway exposes.
	 * There will only be one, since a Twitter account
	 * only ever has one username.
	 */
	private List<MessagingAddress> fromAddresses;
	
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

	@Override
	public List<MessagingAddress> getFromAddresses() {
		return fromAddresses;
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
	}

	@Override
	public void startup() {
		currentCredentials = getCredentials();
		twitter = new Twitter(currentCredentials.getUsername(),currentCredentials.getPassword());
		fromAddresses = new ArrayList<MessagingAddress>();
		fromAddresses.add(new MessagingAddress(currentCredentials.getUsername(),null));
	}

	@Override
	public boolean supportsProtocol(Protocol protocol) {
		return protocol.getProtocolId().equals(TwitterProtocol.PROTOCOL_ID);
	}

	@Override
	public boolean isActive() {
		return twitter != null;
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
