package org.openmrs.module.messaging.twitter;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;

import winterwell.jtwitter.Twitter;

public class TwitterGateway extends MessagingGateway {

	public static final String GATEWAY_ID = "twitter";
	public static final String GATEWAY_NAME = "Twitter";
	
	private Twitter twitter;
	
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
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME);
		String password= Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD);
		Context.closeSession();
		List<MessagingAddress> addresses = new ArrayList<MessagingAddress>();
		addresses.add(new MessagingAddress(username, password,null));
		return addresses;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void startup() {
		MessagingAddress address = getFromAddresses().get(0);
		twitter = new Twitter(address.getAddress(),address.getPassword());
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
