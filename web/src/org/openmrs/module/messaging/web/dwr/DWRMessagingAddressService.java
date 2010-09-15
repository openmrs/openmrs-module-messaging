package org.openmrs.module.messaging.web.dwr;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.schema.exception.AddressFormattingException;

public class DWRMessagingAddressService {

	Log log = LogFactory.getLog(getClass());
	
	private MessagingAddressService addressService;
	private PersonService personService;
	
	public DWRMessagingAddressService(){
		addressService = Context.getService(MessagingAddressService.class);
		personService= Context.getPersonService();
	}

	public List<MessagingAddress> getAllAddressesForPersonId(Integer personId){
		return addressService.getMessagingAddressesForPerson(Context.getPersonService().getPerson(personId));
	}
	
	public List<MessagingAddress> getPublicAddressesForPersonId(Integer personId){
		return addressService.getPublicAddressesForPerson(Context.getPersonService().getPerson(personId));
	}
	
	public List<MessagingAddress> getAllAddressesForCurrentUser(){
		return addressService.getMessagingAddressesForPerson(Context.getAuthenticatedUser().getPerson());
	}
	
	public List<MessagingAddress> getPublicAddressesForCurrentUser(){
		return addressService.getPublicAddressesForPerson(Context.getAuthenticatedUser().getPerson());
	}
	
	public void deleteAddress(Integer id){
		MessagingAddress ma = Context.getService(MessagingAddressService.class).getMessagingAddress(id);
		Context.getService(MessagingAddressService.class).deleteMessagingAddress(ma);
	}
	
	public void saveOrUpdateAddress(MessagingAddress address, Integer personId){
		Protocol protocol = Context.getService(MessagingService.class).getProtocolById(address.getProtocolId());
		MessagingAddress ma = null;
		if(address.getId() == -1){
			try {
				ma = protocol.createAddress(address.getAddress(), Context.getPersonService().getPerson(personId));
				ma.setDateCreated(new Date());
				ma.setCreator(Context.getAuthenticatedUser());
			} catch (AddressFormattingException e) {
				log.error("Error creating address in DWR", e);
			}
		}else{ //otherwise, we're modifying a previously existing address
			ma = Context.getService(MessagingAddressService.class).getMessagingAddress(address.getId());
			ma.setAddress(address.getAddress());
			ma.setProtocolId(address.getProtocolId());
			ma.setDateChanged(new Date());
			ma.setChangedBy(Context.getAuthenticatedUser());
		}
		ma.setPreferred(address.getPreferred());
		ma.setFindable(address.isFindable());
		Context.getService(MessagingAddressService.class).saveMessagingAddress(ma);
	}
	
	public void saveOrUpdateAddressForCurrentUser(MessagingAddress address){
		saveOrUpdateAddress(address,Context.getAuthenticatedUser().getPerson().getId());
	}
}
