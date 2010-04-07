package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.AddressFormattingException;

public class TwitterAddressFactory implements AddressFactory<TwitterAddress> {

	public Boolean addressIsValid(String address) {
		return true;
	}

	public TwitterAddress createAddress(String address, Person person) throws AddressFormattingException {
		return new TwitterAddress(address,"",person);
	}

	public String getFormatHint() {
		return "alphanumeric characters and the underscore (_)";
	}

}
