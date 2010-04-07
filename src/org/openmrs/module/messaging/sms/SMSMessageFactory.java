package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.MessageFactory;

public class SMSMessageFactory implements MessageFactory<SMSMessage,PhoneNumber>{

	public boolean messageContentIsValid(String content) {
		return content.length() <=160;
	}

	public SMSMessage createMessage(String content, PhoneNumber origin, PhoneNumber destination) {
		SMSMessage result = new SMSMessage(destination.getAddress(), content);
		result.setOrigin(origin.getAddress());
		return result;
	}

	public SMSMessage createMessageFromCurrentUser(String content, PhoneNumber destination) {
		return null;
	}

}
