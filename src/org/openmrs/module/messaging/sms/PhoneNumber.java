package org.openmrs.module.messaging.sms;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessagingAddress;

public class PhoneNumber extends MessagingAddress {

	public PhoneNumber(){}
	
	public PhoneNumber(String address, Person person) {
		super(address, person);
	}

	public String getName(){
		return "Phone Number";
	}

	@Override
	public boolean requiresPassword() {
		return false;
	}
	
	
	
}
