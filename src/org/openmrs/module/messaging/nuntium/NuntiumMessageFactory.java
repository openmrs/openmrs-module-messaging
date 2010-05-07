package org.openmrs.module.messaging.nuntium;

import org.openmrs.module.messaging.schema.MessageFactory;
import org.openmrs.module.messaging.schema.MessageFormattingException;

public class NuntiumMessageFactory implements MessageFactory<NuntiumMessage, NuntiumAddress> {

	public NuntiumMessage createMessage(String content, NuntiumAddress origin,
			NuntiumAddress destination) throws MessageFormattingException {
		
		return new NuntiumMessage(destination.getAddress(), content);
	}

	public NuntiumMessage createMessageFromCurrentUser(String content,
			NuntiumAddress destination) throws MessageFormattingException {
		return new NuntiumMessage(destination.getAddress(), content);
	}

	public String getFormattingHint(String currentMessage) {
		return null;
	}

	public boolean messageContentIsValid(String content) {
		return true;
	}
}
