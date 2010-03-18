package org.openmrs.module.messaging.sms;

import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.AddressFormattingException;

public class PhoneNumberFactory implements AddressFactory<PhoneNumber> {

	public Boolean addressIsValid(String address) {
		// TODO Auto-generated method stub
		return null;
	}

	public PhoneNumber createAddress(String address) throws AddressFormattingException {
		return new PhoneNumber(address);
	}

}
