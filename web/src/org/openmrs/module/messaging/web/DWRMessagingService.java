package org.openmrs.module.messaging.web;

import java.util.ArrayList;
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
	
	/**
	 * 
	 * precondition: the addresses being passed in can be sent
	 * by a currently active gateway. (i.e. do not need to check for
	 * gateway activity when returning addresses)
	 * @param address
	 * @param personId
	 * @return
	 */
	public List<String> getToOrFromAddresses(String gatewayString, Integer personId){
		MessagingGateway gateway = MessagingService.getInstance().getMessagingGatewayForName(gatewayString);
		Person p = null;
		if(personId == null){
			p = Context.getAuthenticatedUser().getPerson();
		}else{
			p = Context.getPersonService().getPerson(personId);
		}
		List<MessagingAddress> addresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(p, gateway);
		List<String> results = new ArrayList<String>();
		for(MessagingAddress ma: addresses){
			results.add(ma.getAddress());
		}
		return results;
	}
	
	public List<String> getGatewaysForAddress(String address){
		List<MessagingGateway> gateways = MessagingService.getInstance().getMessagingGatewaysForAddress(address);
		List<String> results = new ArrayList<String>();
		for(MessagingGateway mg: gateways){
			results.add(mg.getName());
		}
		return results;
	}
	
	
}
