package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.MessageFactory;

public class SMSMessageFactory implements MessageFactory<SMSMessage,PhoneNumber>{

	public boolean messageContentIsValid(String content) {
		return false;
	}

	public SMSMessage createMessage(String content, PhoneNumber origin, PhoneNumber destination) {
		return null;
	}

	public SMSMessage createMessageFromCurrentUser(String content, PhoneNumber destination) {
		return null;
	}

}
