package org.openmrs.module.messaging.web;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.MessagingService;

public class DWRMessagingService {

	public String[] getCompatibleAddresses(String fromAddress,String personId){
		Person person = Context.getPersonService().getPerson(Integer.parseInt(personId));
		MessagingGateway gateway = MessagingService.getInstance().getMessagingGatewaysForAddress(fromAddress).get(0);
		List<MessagingAddress> addresses= Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(person, gateway);
		String[] results = new String[addresses.size()];
		for(int i = 0; i < addresses.size();i++){
			results[i] = addresses.get(i).getAddress();
		}
		return results;
		
	}
	
	public boolean requiresPassword(String addressType){
		try {
			Boolean ret = ((MessagingAddress) MessagingService.getInstance().getAddressClassForAddressTypeName(addressType).newInstance()).requiresPassword();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
