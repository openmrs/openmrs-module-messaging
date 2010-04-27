package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.BaseAddressFactory;

import winterwell.jtwitter.Twitter;

public class TwitterAddressFactory extends BaseAddressFactory<TwitterAddress> {

	public TwitterAddressFactory(){}
	
	protected static Twitter twitter = new Twitter();
	
	public Boolean addressIsValid(String address) {
		return address.matches("[a-zA-Z0-9_]{1,15}");
	}
	
	public Boolean usernameExists(String address){
		return twitter.userExists(address);
	}

	public TwitterAddress createAddress(String address, Person person) throws AddressFormattingException {
		if(!addressIsValid(address)){
			throw new AddressFormattingException("The username must be from 1 to 15 characters long and include only letters, numbers, and underscores");
		}else if(!usernameExists(address)){
			throw new AddressFormattingException("That username does not exist");
		}
		return new TwitterAddress(address,"",person);
	}

	public String getFormatHint(String currentAddress) {
		return "alphanumeric characters and the underscore (_)";
	}
}
