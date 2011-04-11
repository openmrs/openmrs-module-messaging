package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.Protocol;

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
	
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person){
		return dao.findMessagingAddresses(null, null, person, false);
	}
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person, boolean includeVoided) {
		return dao.findMessagingAddresses(null,null,person, includeVoided);
	}

	public List<MessagingAddress> findMessagingAddresses(String search, boolean includeVoided) {
		return dao.findMessagingAddresses(search,null,null, includeVoided);
	}
	
	public MessagingAddress getPreferredMessagingAddressForPerson(Person person) {
		return dao.getPreferredMessagingAddressForPerson(person);
	}
	
	public List<MessagingAddress> findMessagingAddresses(String address, Class<? extends Protocol> protocol, Person person, boolean includeVoided){
		return dao.findMessagingAddresses(address,protocol,person, includeVoided);
	}
	
	public void saveMessagingAddress(MessagingAddress address) throws APIException {
		dao.saveMessagingAddress(address);
	}

	public void deleteMessagingAddress(MessagingAddress address) throws APIException {
		dao.deleteMessagingAddress(address);
	}
	
	public void voidMessagingAddress(MessagingAddress address, String reason) throws APIException {
		dao.voidMessagingAddress(address,reason);
	}

	public void unvoidMessagingAddress(MessagingAddress address) throws APIException {
		dao.unvoidMessagingAddress(address);
	}

	public Person getPersonForAddress(String address) {
		return dao.getPersonForAddress(address);
	}

	public MessagingAddress getMessagingAddress(String address) {
		return dao.getMessagingAddress(address);
	}

	public List<MessagingAddress> getPublicAddressesForPerson(Person p) {
		return dao.getPublicAddressesForPerson(p);
	}
}
