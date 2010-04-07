package org.openmrs.module.messaging.sms;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessagingAddress;

public class PhoneNumber extends MessagingAddress {

	public PhoneNumber(String address, Person person) {
		super(address, person);
	}

	protected PhoneNumber(){}
	
}
