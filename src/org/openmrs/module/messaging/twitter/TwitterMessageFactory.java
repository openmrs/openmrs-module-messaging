package org.openmrs.module.messaging.twitter;

import org.openmrs.module.messaging.schema.MessageFactory;
import org.openmrs.module.messaging.schema.MessageFormattingException;

public class TwitterMessageFactory implements MessageFactory<TwitterMessage,TwitterAddress>{
	
	public TwitterMessageFactory(){}
	
	public TwitterMessage createMessage(String content, TwitterAddress origin, TwitterAddress destination) throws MessageFormattingException {
		if(!messageContentIsValid(content)){
			throw new MessageFormattingException("Your Twitter message is too long. Please only use 140 characters");
		}
		TwitterMessage result = new TwitterMessage(destination.getAddress(),content);
		if(origin != null){
			result.setOrigin(origin.getAddress());
			result.setSender(origin.getPerson());
		}
		return result;
	}

	public TwitterMessage createMessageFromCurrentUser(String content, TwitterAddress destination) throws MessageFormattingException{
		return null;
	}

	public boolean messageContentIsValid(String content) {
		return content.length() <=140;
	}

	@Override
	public String getFormattingHint(String currentMessage) {
		return (140 - currentMessage.length()) + " characters left";
	}

}
