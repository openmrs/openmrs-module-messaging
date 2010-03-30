package org.openmrs.module.messaging.twitter;

import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessageFactory;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

import winterwell.jtwitter.Twitter;

public class TwitterMessagingService extends MessagingService<TwitterMessage, TwitterAddress> {

	protected Twitter twitter;
	
	/**
	 * This is used to get the current twitter session. If the currently
	 * authenticated user has a twitter address, it returns a session
	 * with that address. Otherwise, it returns a session with the default
	 * sender address
	 * @return
	 */
	protected Twitter getTwitterSession(){
		if(Context.getAuthenticatedUser() != null){
			List<MessagingAddress> addresses = ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPersonAndService(Context.getAuthenticatedUser(), this);
			if(addresses != null && addresses.size() >0){
				TwitterAddress address = (TwitterAddress) addresses.get(0);
				//if the session isn't already running, make one
				if(!twitter.getScreenName().equals(address.getAddress())){
					twitter = new Twitter(address.getAddress(),address.getPassword());
				}
			}else{
				twitter = new Twitter(getDefaultSenderAddress().getAddress(),getDefaultSenderAddress().getPassword());
			}
		}
		return twitter;
	}
	@Override
	public boolean canReceive() {
		return getTwitterSession() != null && getTwitterSession().isValidLogin();
	}

	@Override
	public boolean canSend() {
		return false;
	}

	@Override
	public AddressFactory getAddressFactory() {
		return null;
	}

	@Override
	public TwitterAddress getDefaultSenderAddress() {
		if(Context.getAuthenticatedUser() != null){
			List<MessagingAddress> addresses = ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPersonAndService(Context.getAuthenticatedUser(), this);
			if(addresses != null && addresses.size() >0){
				return (TwitterAddress) addresses.get(0);
			}
		}
		return new TwitterAddress("devtestingjuan", "martyr441", null);
	}

	@Override
	public String getDescription() {
		return "Allows users to send and receive twitter messages";
	}

	@Override
	public MessageFactory getMessageFactory() {
		return null;
	}

	@Override
	public String getName() {
		return "Twitter";
	}
	
	public void changeStatus(String status){
		getTwitterSession().setStatus(status);
	}

	@Override
	public void sendMessage(String address, String content) {
		if(getAddressFactory().addressIsValid(address) && getMessageFactory().messageContentIsValid(content)){
			getTwitterSession().sendMessage(address, content);
		}
		TwitterMessage m = new TwitterMessage(address,content);
		m.setOrigin(getDefaultSenderAddress().getAddress());
		m.setDateSent(new Date());
		m.setDateReceived(new Date());
		((MessageService) Context.getService(MessageService.class)).saveMessage(m);
	}
	

	@Override
	public void sendMessage(TwitterMessage message) {
		getTwitterSession().sendMessage(message.getDestination(), message.getContent());
		message.setOrigin(getDefaultSenderAddress().getAddress());
		message.setDateReceived(new Date());
		saveMessage(message);
	}

	@Override
	public void sendMessage(TwitterMessage message, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessageToAddresses(TwitterMessage m, List<String> addresses, MessageDelegate delegate) {
		for(String address:addresses){
			getTwitterSession().sendMessage(address, m.getContent());
		}
	}

	@Override
	public void sendMessages(List<TwitterMessage> messages, MessageDelegate delegate) {
		for(TwitterMessage m: messages){
			getTwitterSession().sendMessage(m.getDestination(), m.getContent());
		}
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void startup() {
		twitter = new Twitter(getDefaultSenderAddress().getAddress(),getDefaultSenderAddress().getPassword());
	}

}
