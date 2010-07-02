package org.openmrs.module.messaging.web.dwr;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.schema.exception.AddressFormattingException;

public class DWRMessagingAddressService {

	Log log = LogFactory.getLog(getClass());
	
	private MessagingAddressService addressService;
	
	public DWRMessagingAddressService(){
		addressService = Context.getService(MessagingAddressService.class);
	}
	//TODO: Make this work
	public boolean requiresPassword(String protocolId){
		Protocol p = MessagingService.getProtocolById(protocolId);
		if(p != null){
			return false;
		}
		return false;
	}
	
	public List<MessagingAddress> getAllAddressesForPerson(Person p){
		return addressService.getMessagingAddressesForPerson(p);
	}
	
	public List<MessagingAddress> getAllAddressesForCurrentUser(){
		return getAllAddressesForPerson(Context.getAuthenticatedUser().getPerson());
	}
	
	public List<MessagingAddress> getPublicAddressesForPerson(Person p){
		return addressService.getPublicAddressesForPerson(p);
	}
	
	public List<MessagingAddress> getPublicAddressesForCurrentUser(){
		return getPublicAddressesForPerson(Context.getAuthenticatedUser().getPerson());
	}
	
	public void deleteAddress(Integer id){
		MessagingAddress ma = Context.getService(MessagingAddressService.class).getMessagingAddress(id);
		Context.getService(MessagingAddressService.class).deleteMessagingAddress(ma);
	}
	
	public void saveOrUpdateAddress(MessagingAddress address, Person p){
		Protocol protocol = MessagingService.getProtocolById(address.getProtocolId());
		MessagingAddress ma = null;
		if(address.getId() == -1){
			try {
				ma = protocol.createAddress(address.getAddress(), p);				
			} catch (AddressFormattingException e) {
				log.error("Error creating address in DWR", e);
			}
		}else{
			ma = Context.getService(MessagingAddressService.class).getMessagingAddress(address.getId());
		}
		ma.setPreferred(address.getPreferred());
		ma.setFindable(address.isFindable());
		if(address.getPassword() != null && !address.getPassword().equals("")){
			ma.setPassword(address.getPassword());
		}
		Context.getService(MessagingAddressService.class).saveMessagingAddress(ma);
	}
	
	public void saveOrUpdateAddressForCurrentUser(MessagingAddress address){
		saveOrUpdateAddress(address,Context.getAuthenticatedUser().getPerson());
	}
}
