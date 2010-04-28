package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessagingAddress;

public class TwitterAddress extends MessagingAddress{

	public TwitterAddress(String address, String password, Person person) {
		super(address, password, person);
	}
	
	public TwitterAddress(String address, String password) {
		super(address, password,null);
	}
	
	public String getName(){
		return "Twitter Username";
	}
	
	public TwitterAddress(){}

	@Override
	public boolean requiresPassword() {
		return true;
	}
}
