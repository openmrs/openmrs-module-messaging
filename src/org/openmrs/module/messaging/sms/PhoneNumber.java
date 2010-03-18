package org.openmrs.module.messaging.sms;

import java.util.List;

import org.openmrs.module.messaging.schema.MessageAddress;

public class PhoneNumber extends MessageAddress {

	public PhoneNumber(String number){
		super(number);
	}
	
	public List<MessageAddress> findPossibleValues(String searchText) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MessageAddress> getPossibleValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageAddress hydrate(String s) {
		// TODO Auto-generated method stub
		return null;
	}

}
