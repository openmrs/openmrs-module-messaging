package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;

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
	
	public List<MessagingAddress> getMessagingAddressesForGateway(MessagingGateway gateway) {
		return dao.getMessagingAddressesForGateway(gateway);
	}
	
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person) {
		return dao.getMessagingAddressesForPerson(person);
	}

	public List<MessagingAddress> getMessagingAddressesForPersonAndGateway(Person person, MessagingGateway gateway) {
		return dao.getMessagingAddressesForPersonAndGateway(person, gateway);
	}

	public List<MessagingAddress> findMessagingAddresses(String search) {
		return dao.findMessagingAddresses(search);
	}

	public List<MessagingAddress> findMessagingAddresses(String search, MessagingGateway gateway) {
		return dao.findMessagingAddresses(search,gateway);
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
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForClass(Class<? extends A> addressClass){
		return dao.getMessagingAddressesForClass(addressClass);
	}
	
	public List<MessagingAddress> getMessagingAddressesForTypeName(String typeName){
		return dao.getMessagingAddressesForTypeName(typeName);
	}
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForPersonAndClass(Person person, Class<? extends A> addressClass){
		return dao.getMessagingAddressesForPersonAndClass(person, addressClass);
	}
	
	public List<MessagingAddress> getMessagingAddressesForPersonAndTypeName(Person person, String typeName){
		return dao.getMessagingAddressesForPersonAndTypeName(person, typeName);
	}

}
