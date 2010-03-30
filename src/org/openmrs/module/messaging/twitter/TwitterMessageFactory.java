package org.openmrs.module.messaging.twitter;

import org.openmrs.module.messaging.schema.MessageFactory;

public class TwitterMessageFactory implements MessageFactory<TwitterMessage,TwitterAddress>{

	public TwitterMessage createMessage(String content, TwitterAddress origin,
			TwitterAddress destination) {
		return null;
	}

	public TwitterMessage createMessageFromCurrentUser(String content, TwitterAddress destination) {
		return null;
	}

	public boolean messageContentIsValid(String content) {
		return content.length() <=140;
	}

}
