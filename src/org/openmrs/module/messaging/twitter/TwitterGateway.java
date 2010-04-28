package org.openmrs.module.messaging.twitter;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessageFormattingException;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;

import winterwell.jtwitter.Twitter;


public class TwitterGateway extends MessagingGateway<TwitterMessage, TwitterAddress> {

	protected static Log log = LogFactory.getLog(TwitterGateway.class);
	
	public TwitterGateway(){}
	
	protected Twitter twitter;
	
	protected static TwitterAddressFactory twitterAddressFactory = new TwitterAddressFactory();
	
	protected static TwitterMessageFactory twitterMessageFactory = new TwitterMessageFactory();

	@Override
	public TwitterAddressFactory getAddressFactory() {
		return twitterAddressFactory; 
	}
	@Override
	public TwitterMessageFactory getMessageFactory() {
		return twitterMessageFactory;
	}
	
	public TwitterAddress getDefaultSenderAddress(){
		String uname = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME);
		String pword = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD);
		return new TwitterAddress(uname,pword);
	}
	
	/**
	 * Returns the Twitter Address of the currently authenticated user,
	 *  if there is one. If there is not, it returns null
	 * @return
	 */
	protected TwitterAddress getCurrentUserTwitterAddress(){
		TwitterAddress result = null;
		if(Context.getAuthenticatedUser() != null){
			List<MessagingAddress> addresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(Context.getAuthenticatedUser().getPerson(), this);
			if(addresses != null && addresses.size() > 0){
				result = (TwitterAddress) addresses.get(0);
			}
		}
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
		try{
			return getCurrentTwitterSession().isValidLogin();
		}catch(Exception e){
			log.error("Error with twitter",e);
			return false;
		}
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
		twitter.updateStatus(status);
	}

	@Override
	public void sendMessage(String address, String content) {
		//set header info
		TwitterMessage m = new TwitterMessage(address,content);
		m.setOrigin(getCurrentTwitterAddress().getAddress());
		m.setDateSent(new Date());
		m.setDateReceived(new Date());
		//set recipient, if we can find it
		Person p = Context.getService(MessagingAddressService.class).getPersonForAddress(address);
		if(m.getRecipient() == null && p != null){
			m.setRecipient(p);
		}
		m.setGatewayId(getGatewayId());
		//save message
		Context.getService(MessageService.class).saveMessage(m);
		if(getAddressFactory().addressIsValid(address) && getMessageFactory().messageContentIsValid(content)){
			getCurrentTwitterSession().sendMessage(address, content);
		}
	}
	

	@Override
	public void sendMessage(TwitterMessage message) throws MessageFormattingException{
		//make sure that the currently authenticated user is following the 
		//person that they want to send a message to
		Twitter twitSession;
		if(message.getOrigin() != null && !message.getOrigin().equals("") && message.getOrigin().equals(getDefaultSenderAddress().getAddress())){
			TwitterAddress fromAddress = (TwitterAddress) Context.getService(MessagingAddressService.class).getMessagingAddress(message.getOrigin());
			twitSession = new Twitter(fromAddress.getAddress(),fromAddress.getPassword());
		}else{
			twitSession = getCurrentTwitterSession();
		}
		if(!twitSession.isFollowing(message.getDestination())
		|| !twitSession.isFollower(message.getDestination())){
			throw new MessageFormattingException("Could not send the direct message - either you are not following that user or they are not following you.");
		}
		//set the origin of the message
		message.setOrigin(twitSession.getScreenName());
		message.setDateSent(new Date());
		message.setDateReceived(new Date());
		//set the recipient, if we can find them
		Person p = Context.getService(MessagingAddressService.class).getPersonForAddress(message.getDestination());
		if(message.getRecipient() == null && p != null){
			message.setRecipient(p);
		}
		if(message.getSender() == null && Context.getAuthenticatedUser() !=null){
			message.setSender(Context.getAuthenticatedUser().getPerson());
		}
		message.setGatewayId(getGatewayId());
		//save the message
		Context.getService(MessageService.class).saveMessage(message);
		//send the message
		twitSession.sendMessage(message.getDestination(), message.getContent());
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
		
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void startup() {
		twitter= new Twitter(getDefaultSenderAddress().getAddress(), getDefaultSenderAddress().getPassword());
	}
	@Override
	public String getGatewayId() {
		return "twitter";
	}
	@Override
	public boolean canSendFromUserAddresses() {
		return true;
	}

}
