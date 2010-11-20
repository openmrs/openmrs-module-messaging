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

	/**
	 * Boolean indicating whether or not this gateway is connected to Twitter
	 */
	private volatile boolean isActive = false;
	
	/**
	 * Boolean used for starting and stopping the activity-checking thread
	 */
	private volatile boolean stopThread = false;

	private static Log log = LogFactory.getLog(TwitterGateway.class);

	/**
	 * Object that encapsulates a session with the Twitter API
	 */
	private Twitter twitter;

	/**
	 * The in-use login credentials
	 */
	private CredentialSet currentCredentials;

	@Override
	public String getName() {
		return GATEWAY_NAME;
	}

	@Override
	public String getDescription() {
		return Context.getMessageSourceService().getMessage(
				"messaging.twitter.gateway.description");
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
	 * Returns a pair of strings. The first string is the username and the
	 * second string is the password
	 * 
	 * @return
	 */
	private CredentialSet getCredentials() {
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(
				MessagingConstants.GP_DEFAULT_TWITTER_UNAME);
		String password = Context.getAdministrationService().getGlobalProperty(
				MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD);
		Context.closeSession();
		CredentialSet credentials = new CredentialSet(username, password);
		return credentials;
	}

	public void updateCredentials(String username, String password) {
		this.currentCredentials = new CredentialSet(username, password);
		this.twitter = new Twitter(username, password);
	}

	@Override
	public void shutdown() {
		twitter = null;
		stopThread();
	}

	@Override
	public void startup() {
		currentCredentials = getCredentials();
		twitter = new Twitter(currentCredentials.getUsername(),currentCredentials.getPassword());
		startActivityCheckingThread();
	}

	/**
	 * Starts a thread that polls twitter every 2 seconds to see if the gateway
	 * is still connected.
	 */
	private void startActivityCheckingThread() {
		stopThread = false;
		Thread activeCheckingThread = new Thread(new Runnable() {
			public void run() {
				while (!stopThread) {
					if (twitter != null) {
						try {
							if (twitter.isValidLogin()) {
								setActive(true);
							}
						} catch (Throwable t) {
							setActive(false);
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							log.error("Error sleeping in Twitter activity checking thread",e);
						}
					}
				}
			}
		});
		activeCheckingThread.start();
	}

	private void stopThread() {
		stopThread = true;
	}

	@Override
	public boolean supportsProtocol(Protocol protocol) {
		return protocol.getProtocolId().equals(TwitterProtocol.class.getName());
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	private void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public void sendMessage(Message message) {
		if (twitter != null)
			twitter.sendMessage(message.getDestination(), message.getContent());
	}

	@Override
	public void recieveMessages() {
		// DO NOTHING
	}

}
