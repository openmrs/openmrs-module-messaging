package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.Protocol;

public class MessagingAddressServiceImpl extends BaseOpenmrsService implements MessagingAddressService {

	protected MessagingAddressDAO dao;
	
	public void setMessagingAddressDAO(MessagingAddressDAO dao) {
		this.dao = dao;
	}	
	
	public List<MessagingAddress> getAllMessagingAddresses(){
		return dao.getAllMessagingAddresses();
	}
	
	public MessagingAddress getMessagingAddress(Integer addressId){
		return dao.getMessagingAddress(addressId);
	}
	
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person) {
		return dao.findMessagingAddresses(null,null,person);
	}

	public List<MessagingAddress> findMessagingAddresses(String search) {
		return dao.findMessagingAddresses(search,null,null);
	}
	
	public MessagingAddress getPreferredMessagingAddressForPerson(Person person) {
		return dao.getPreferredMessagingAddressForPerson(person);
	}
	
	public List<MessagingAddress> findMessagingAddresses(String address, Protocol protocol, Person person){
		return dao.findMessagingAddresses(address,protocol,person);
	}
	
	public void saveMessagingAddress(MessagingAddress address) throws APIException {
		dao.saveMessagingAddress(address);
	}

	public void deleteMessagingAddress(MessagingAddress address) throws APIException {
		dao.deleteMessagingAddress(address);
	}
	
	public void retireMessagingAddress(MessagingAddress address, String reason) throws APIException {
		dao.voidMessagingAddress(address,reason);
	}

	public void unretireMessagingAddress(MessagingAddress address) throws APIException {
		dao.unvoidMessagingAddress(address);
	}

	public Person getPersonForAddress(String address) {
		return dao.getPersonForAddress(address);
	}

	public MessagingAddress getMessagingAddress(String address) {
		return dao.getMessagingAddress(address);
	}
}
