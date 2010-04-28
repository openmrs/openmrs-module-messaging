package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.Message;

public class SmsMessage extends Message {
	public SmsMessage(String destination, String content) {
		super(destination, content);
	}
	
	protected SmsMessage(){}
}
