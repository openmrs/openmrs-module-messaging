package org.openmrs.module.messaging.nuntium;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.BaseAddressFactory;

public class NuntiumAddressFactory extends BaseAddressFactory<NuntiumAddress> {

	public Boolean addressIsValid(String address) {
		return Boolean.TRUE;
	}

	public NuntiumAddress createAddress(String address, Person person)
			throws AddressFormattingException {
		
		return new NuntiumAddress(address, person);
		
	}
	
	public String getFormatHint(String currentAddress) {
		return null;
	}

}
