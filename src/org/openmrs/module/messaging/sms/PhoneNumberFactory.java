package org.openmrs.module.messaging.sms;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.AddressFormattingException;

public class PhoneNumberFactory implements AddressFactory<PhoneNumber> {

	public Boolean addressIsValid(String address) {
		return true;
	}

	public PhoneNumber createAddress(String address,Person person) throws AddressFormattingException {
		return new PhoneNumber(address, person);
	}

	public String getFormatHint() {
		return "###.###.####";
	}

}
