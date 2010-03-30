package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessagingAddress;

public class TwitterAddress extends MessagingAddress{

	public TwitterAddress(String address, String password, Person person) {
		super(address, password, person);
	}

}
