package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

public class MessagingAddressServiceImpl extends BaseOpenmrsService implements MessagingAddressService {

	protected MessagingAddressDAO dao;
	
	public void setMessageAddressDAO(MessagingAddressDAO dao) {
		this.dao = dao;
	}	
	
	public List<MessagingAddress> getAllMessagingAddresses(){
		return dao.getAllMessagingAddresses();
	}
	
	public MessagingAddress getMessagingAddress(Integer addressId){
		return dao.getMessagingAddress(addressId);
	}
	
	public List<MessagingAddress> getMessagingAddressesForService(MessagingService service) {
		return dao.getMessagingAddressesForService(service);
	}
	
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person) {
		return dao.getMessagingAddressesForPerson(person);
	}

	public List<MessagingAddress> getMessagingAddressesForPersonAndService(Person person, MessagingService service) {
		return dao.getMessagingAddressesForPersonAndService(person, service);
	}

	public List<MessagingAddress> findMessagingAddresses(String search) {
		return dao.findMessagingAddresses(search);
	}

	public List<MessagingAddress> findMessagingAddresses(String search, MessagingService service) {
		return dao.findMessagingAddresses(search,service);
	}

	public MessagingAddress getPreferredMessagingAddressForPerson(Person person) {
		return dao.getPreferredMessagingAddressForPerson(person);
	}
	
	public void saveMessagingAddress(MessagingAddress address) throws APIException {
		dao.saveMessagingAddress(address);
	}

	public void deleteMessagingAddress(MessagingAddress address) throws APIException {
		dao.deleteMessagingAddress(address);
	}
	
	public void retireMessagingAddress(MessagingAddress address, String reason) throws APIException {
		dao.retireMessagingAddress(address,reason);
	}

	public void unretireMessagingAddress(MessagingAddress address) throws APIException {
		dao.unretireMessagingAddress(address);
	}

}
