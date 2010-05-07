package org.openmrs.module.messaging.nuntium;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessagingAddress;

public class NuntiumAddress extends MessagingAddress {

	public NuntiumAddress(String address, Person person) {
		super(address, person);
	}
	
	public NuntiumAddress() {
	}
	
	@Override
	public String getName() {
		return "Nuntium address";
	}

	@Override
	public boolean requiresPassword() {
		return false;
	}

}
