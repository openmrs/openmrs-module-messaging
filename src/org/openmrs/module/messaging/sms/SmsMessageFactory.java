package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.MessageFactory;
import org.openmrs.module.messaging.schema.MessageFormattingException;

public class SmsMessageFactory implements MessageFactory<SmsMessage,PhoneNumber>{

	public SmsMessage createMessage(String content, PhoneNumber origin, PhoneNumber destination) throws MessageFormattingException {
		SmsMessage m;
		if(messageContentIsValid(content)){
			m = new SmsMessage(destination.getAddress(),content);
			if(origin != null){
				m.setOrigin(origin.getAddress());
				
			}
		}else{
			throw new MessageFormattingException("Message is longer than 160 characters");
		}
		return m;
	}

	public SmsMessage createMessageFromCurrentUser(String content, PhoneNumber destination) throws MessageFormattingException {
		return null;
	}

	public String getFormattingHint(String currentMessage) {
		return "";
	}

	public boolean messageContentIsValid(String content) {
		return content.length() <=160;
	}

}
