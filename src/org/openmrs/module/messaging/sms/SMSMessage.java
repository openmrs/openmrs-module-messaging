package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.Message;

public class SMSMessage extends Message<PhoneNumber> {

	public SMSMessage(PhoneNumber destination, String content) {
		super(destination, content);
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

}
