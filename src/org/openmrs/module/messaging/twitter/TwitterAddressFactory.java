package org.openmrs.module.messaging.twitter;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.AddressFormattingException;

public class TwitterAddressFactory implements AddressFactory<TwitterAddress> {

	public Boolean addressIsValid(String address) {
		return null;
	}

	public TwitterAddress createAddress(String address, Person person) throws AddressFormattingException {
		return null;
	}

	public String getFormatHint() {
		return null;
	}

}
