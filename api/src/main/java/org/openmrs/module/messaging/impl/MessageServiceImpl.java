package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public class MessageServiceImpl extends BaseOpenmrsService implements MessageService {

	protected MessageDAO dao;
	
	public void setMessageDAO(MessageDAO dao) {
		this.dao = dao;
	}	
	
	public List<Message> getAllMessages() {
		return dao.getAllMessages();
	}
	
	public Message getMessage(Integer messageId) {
		return dao.getMessage(messageId);
	}
	
	public List<Message> getMessagesToPerson(Person recipient) {
		return dao.findMessagesWithPeople(null, recipient,null, null, null);
	}
	
	public List<Message> getMessagesFromPerson(Person sender) {
		return dao.findMessagesWithPeople(null, null,sender, null, null);
	}
	
	public List<Message> getMessagesToOrFromPerson(Person person) {
		return dao.findMessagesWithPeople(null, person,person, null, null);
	}
	
	public List<Message> getMessagesToAddress(String address) {
		return dao.findMessagesWithAddresses(null, address, null, null, null);
	}

	public List<Message> getMessagesFromAddress(String address) {
		return dao.findMessagesWithAddresses(null, null, address, null, null);
	}

	public List<Message> getMessagesToOrFromAddress(String address) {
		return dao.findMessagesWithAddresses(null, address, address, null, null);
	}
	
	public List<Message> findMessages(String content) {
		return dao.findMessagesWithAddresses(null, null, null, content, null);
	}
	
	public List<Message> findMessagesWithAdresses(Class<? extends Protocol> protocol, String toAddress, String fromAddress, String content, Integer status) {
		return dao.findMessagesWithAddresses(protocol,toAddress,fromAddress,content,status);
	}


	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocol, Person sender, Person recipient, String content, Integer status) {
		return dao.findMessagesWithPeople(protocol, sender, recipient, content, status);
	}

	public void deleteMessage(Message message) throws APIException {
		dao.deleteMessage(message);
	}

	public void saveMessage(Message message) throws APIException {
		dao.saveMessage(message);
	}

	public List<Message> getMessagesForPersonAndProtocol(Person person, Class<? extends Protocol> protocol) {
		return dao.findMessagesWithPeople(protocol, person, person, null, null);
	}

	public List<Message> getOutboxMessages() {
		return dao.getOutboxMessages();
	}

	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocol) {
		return dao.getOutboxMessagesByProtocol(protocol);
	}
	
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to, boolean dateOrderAscending, Class<? extends Protocol> protocolClass){
		return dao.getMessagesForPersonPaged(pageNumber, pageSize, personId, to, dateOrderAscending, protocolClass);
	}

	public List<Message> getMessagesForProtocolAndStatus(Class<? extends Protocol> protocol, Integer status) {
		return dao.findMessagesWithPeople(protocol, null, null, null, status);
	}
	
	public Integer countMessagesForPerson(int personId, boolean to, Class<? extends Protocol> protocolClass){
		return dao.countMessagesForPerson(personId, to,protocolClass);
	}

	public List<Message> getMessagesForAddress(String address, boolean to) {
		if(to) return dao.findMessagesWithAddresses(null, address, null, null, null);
		else return dao.findMessagesWithAddresses(null, null, address, null, null);
	}

	public List<Message> getMessagesForPerson(Person person, boolean to) {
		if(to) return dao.findMessagesWithPeople(null, null, person, null,null);
		else return dao.findMessagesWithPeople(null, person, null, null, null);
	}
	
	public List<Message> searchMessages(int pageNumber, int pageSize, String searchString, Person p, boolean inbox, boolean outbox, boolean orderDateAscending){
		return dao.searchMessages(pageNumber, pageSize, searchString, p, inbox,outbox, orderDateAscending);
	}

	public Integer countSearch(Person p, String searchString, boolean inbox, boolean outbox) {
		return dao.countSearch(p, searchString, inbox, outbox);
	}
}
