package org.openmrs.module.messaging.twitter;

import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessagingService;

import winterwell.jtwitter.Twitter;


public class TwitterMessagingService extends MessagingService<TwitterMessage, TwitterAddress> {

	protected Twitter twitter;
	
	protected TwitterAddressFactory twitterAddressFactory;
	
	protected TwitterMessageFactory twitterMessageFactory;
	
	public void setTwitterAddressFactory(TwitterAddressFactory twitterAddressFactory){
		this.twitterAddressFactory = twitterAddressFactory;
	}
	
	public void setTwitterMessageFactory(TwitterMessageFactory twitterMessageFactory){
		this.twitterMessageFactory = twitterMessageFactory;
	}
	
	@Override
	public TwitterAddressFactory getAddressFactory() {
		return twitterAddressFactory; 
	}
	@Override
	public TwitterMessageFactory getMessageFactory() {
		return twitterMessageFactory;
	}
	
	@Override
	public TwitterAddress getDefaultSenderAddress() {
		return new TwitterAddress("devTestingJuan", "martyr441", null);
	}
	
	/**
	 * Returns the Twitter Address of the currently authenticated user,
	 *  if there is one. If there is not, it returns null
	 * @return
	 */
	protected TwitterAddress getCurrentUserTwitterAddress(){
		TwitterAddress result = null;
//		if(Context.getAuthenticatedUser() != null){
//			List<MessagingAddress> addresses = ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPersonAndService(Context.getAuthenticatedUser(), this);
//			if(addresses != null && addresses.size() > 0){
//				result = new TwitterAddress(addresses.get(0).getAddress(),addresses.get(0).getPassword());
//			}
//		}
		return result;
	}
	
	/**
	 * Returns either the twitter address of the currently authenticated user
	 * or the default twitter address
	 * @return
	 */
	protected TwitterAddress getCurrentTwitterAddress(){
		TwitterAddress result = getCurrentUserTwitterAddress();
		if(result == null){
			result = getDefaultSenderAddress();
		}
		return result;
	}
	/**
	 * This is used to get the current twitter session. If the currently
	 * authenticated user has a twitter address, it returns a session
	 * with that address. Otherwise, it returns a session with the default
	 * sender address
	 * @return
	 */
	protected Twitter getCurrentTwitterSession(){
		TwitterAddress address = getCurrentTwitterAddress();
		//if the session has not been initialized or a session for the user
		//is not already running
		if(twitter == null || !twitter.getScreenName().equals(address.getAddress())){
			twitter = new Twitter(address.getAddress(),address.getPassword());
		}
		return twitter;
	}
	
	@Override
	public boolean canReceive() {
		return getCurrentTwitterSession() != null;
	}

	@Override
	public boolean canSend() {
		return canReceive();
	}

	@Override
	public String getDescription() {
		return "Allows users to send and receive twitter messages";
	}


	@Override
	public String getName() {
		return "Twitter";
	}
	
	public void changeStatus(String status){
		getCurrentTwitterSession().updateStatus(status);
	}

	@Override
	public void sendMessage(String address, String content) {
		if(getAddressFactory().addressIsValid(address) && getMessageFactory().messageContentIsValid(content)){
			getCurrentTwitterSession().sendMessage(address, content);
		}
		TwitterMessage m = new TwitterMessage(address,content);
		m.setOrigin(getCurrentTwitterAddress().getAddress());
		m.setDateSent(new Date());
		m.setDateReceived(new Date());
		((MessageService) Context.getService(MessageService.class)).saveMessage(m);
	}
	

	@Override
	public void sendMessage(TwitterMessage message) {
		getCurrentTwitterSession().sendMessage(message.getDestination(), message.getContent());
		message.setOrigin(getCurrentTwitterAddress().getAddress());
		message.setDateReceived(new Date());
		saveMessage(message);
	}

	@Override
	public void sendMessage(TwitterMessage message, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessageToAddresses(TwitterMessage m, List<String> addresses, MessageDelegate delegate) {
		//TODO
	}

	@Override
	public void sendMessages(List<TwitterMessage> messages, MessageDelegate delegate) {
		//TODO
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void startup() {
		getCurrentTwitterSession();
	}

}
