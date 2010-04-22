package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;

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
		return dao.getMessagesToPerson(recipient);
	}
	
	public List<Message> getMessagesFromPerson(Person sender) {
		return dao.getMessagesFromPerson(sender);
	}
	
	public List<Message> getMessagesToOrFromPerson(Person person) {
		return dao.getMessagesToOrFromPerson(person);
	}
	
	public List<Message> getMessagesToAddress(String address) {
		return dao.getMessagesToAddress(address);
	}

	public List<Message> getMessagesFromAddress(String address) {
		return dao.getMessagesFromAddress(address);
	}

	public List<Message> getMessagesToOrFromAddress(String address) {
		return dao.getMessagesToOrFromAddress(address);
	}
	
	public List<Message> getMessagesForGateway(MessagingGateway gateway) {
		return dao.getMessagesForGateway(gateway);
	}
	
	public List<Message> getMessagesToPersonUsingGateway(MessagingGateway gateway, Person recipient) {
		return dao.getMessagesToPersonUsingGateway(gateway, recipient);
	}
	
	public List<Message> getMessagesFromPersonUsingGateway(MessagingGateway gateway, Person sender) {
		return dao.getMessagesFromPersonUsingGateway(gateway, sender);
	}
	
	public List<Message> getMessagesToOrFromPersonUsingGateway(MessagingGateway gateway, Person person) {
		return dao.getMessagesToOrFromPersonUsingGateway(gateway,person);
	}

	public List<Message> findMessages(String content) {
		return dao.findMessages(content);
	}
	
	public List<Message> findMessagesWithAdresses(MessagingGateway gateway, String toAddress, String fromAddress, String content, Integer status) {
		return dao.findMessagesWithAddresses(gateway,toAddress,fromAddress,content,status);
	}


	public List<Message> findMessagesWithPeople(MessagingGateway gateway, Person sender, Person recipient, String content, Integer status) {
		return dao.findMessagesWithPeople(gateway, sender, recipient, content, status);
	}

	public void deleteMessage(Message message) throws APIException {
		dao.deleteMessage(message);
	}

	public void saveMessage(Message message) throws APIException {
		dao.saveMessage(message);
	}
}
