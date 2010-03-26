package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.Message;

public class SMSMessage extends Message {

	public SMSMessage(String destination, String content) {
		super(destination, content);
	}


}
