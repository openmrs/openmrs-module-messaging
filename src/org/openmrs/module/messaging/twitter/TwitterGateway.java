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
import org.openmrs.module.messaging.schema.MessagingGateway;

import winterwell.jtwitter.Twitter;


public class TwitterGateway extends MessagingGateway<TwitterMessage, TwitterAddress> {

	protected static Log log = LogFactory.getLog(TwitterGateway.class);
	
	public TwitterGateway(){}
	
	protected Twitter twitter;
	
	protected static TwitterAddressFactory twitterAddressFactory = new TwitterAddressFactory();
	
	protected static TwitterMessageFactory twitterMessageFactory = new TwitterMessageFactory();
	
	protected boolean canSend = false;

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
	
	protected Twitter getDefaultTwitterSession(){
		//if the default twitter address has changed since the last time the session
		//was requested, update it
		
		if(twitter == null || !twitter.getScreenName().equals(getDefaultSenderAddress().getAddress())){
			twitter = new Twitter(getDefaultSenderAddress().getAddress(),getDefaultSenderAddress().getPassword());
			canSend = twitter.isValidLogin();
		}
		return twitter;
	}
	@Override
	public boolean canReceive() {
		return canSend();
	}

	@Override
	public boolean canSend() {
		return canSend;
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
	public void sendMessage(String address, String content) throws MessageFormattingException {
		if(getDefaultTwitterSession().isFollowing(address) || !getDefaultTwitterSession().isFollower(address)){
			throw new MessageFormattingException("Could not send the direct message - either you are not following that user or they are not following you.");
		}
		//set header info
		TwitterMessage m = new TwitterMessage(address,content);
		m.setOrigin(getDefaultSenderAddress().getAddress());
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
			getDefaultTwitterSession().sendMessage(address, content);
		}
	}
	

	@Override
	public void sendMessage(TwitterMessage message) throws MessageFormattingException{
		//get the twitter session that we will be using
		Twitter twitSession;
		//if there is an origin in the message and the origin is not the default address
		//then create a new twitter session for that origin
		if(message.getOrigin() != null && !message.getOrigin().equals("") && !message.getOrigin().equals(getDefaultSenderAddress().getAddress())){
			TwitterAddress fromAddress = (TwitterAddress) Context.getService(MessagingAddressService.class).getMessagingAddress(message.getOrigin());
			twitSession = new Twitter(fromAddress.getAddress(),fromAddress.getPassword());
		}else{
			twitSession = getDefaultTwitterSession();
		}
		//if the twitter user that is designated as the sender is not following and followed by
		//the designated recipient, then throw an exception
		if(!twitSession.isFollowing(message.getDestination()) || !twitSession.isFollower(message.getDestination())){
			throw new MessageFormattingException("Could not send the direct message - either you are not following that user or they are not following you.");
		}
		//set header info of the message
		message.setOrigin(twitSession.getScreenName());
		message.setDateSent(new Date());
		message.setDateReceived(new Date());
		//set the recipient, if we can find them
		Person p = Context.getService(MessagingAddressService.class).getPersonForAddress(message.getDestination());
		if(message.getRecipient() == null && p != null){
			message.setRecipient(p);
		}
		message.setGatewayId(getGatewayId());
		//save the message
		Context.getService(MessageService.class).saveMessage(message);
		//send the message
		twitSession.sendMessage(message.getDestination(), message.getContent());
	}

	@Override
	public void sendMessage(TwitterMessage message, MessageDelegate delegate) {
		
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
		getDefaultTwitterSession();
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
