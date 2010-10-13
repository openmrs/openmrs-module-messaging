package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.Protocol;

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
	
	public List<Message> findMessagesWithAdresses(Protocol protocol, String toAddress, String fromAddress, String content, Integer status) {
		return dao.findMessagesWithAddresses(protocol,toAddress,fromAddress,content,status);
	}


	public List<Message> findMessagesWithPeople(Protocol protocol, Person sender, Person recipient, String content, Integer status) {
		return dao.findMessagesWithPeople(protocol, sender, recipient, content, status);
	}

	public void deleteMessage(Message message) throws APIException {
		dao.deleteMessage(message);
	}

	public void saveMessage(Message message) throws APIException {
		dao.saveMessage(message);
	}

	public List<Message> getMessagesForPersonAndProtocol(Person person, Protocol protocol) {
		return dao.findMessagesWithPeople(protocol, person, person, null, null);
	}

	public List<Message> getOutboxMessages() {
		return dao.getOutboxMessages();
	}

	public List<Message> getOutboxMessagesByProtocol(Protocol p) {
		return dao.getOutboxMessagesByProtocol(p);
	}
}
