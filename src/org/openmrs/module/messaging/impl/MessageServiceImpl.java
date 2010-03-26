package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingService;

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
		return dao. getMessagesToPerson(recipient);
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
	
	public List<Message> getMessagesForService(MessagingService service) {
		return dao.getMessagesForService(service);
	}
	
	public List<Message> getMessagesToPersonUsingService(MessagingService service, Person recipient) {
		return dao.getMessagesToPersonUsingService(service, recipient);
	}
	
	public List<Message> getMessagesFromPersonUsingService(MessagingService service, Person sender) {
		return dao.getMessagesFromPersonUsingService(service, sender);
	}
	
	public List<Message> getMessagesToOrFromPersonUsingService(MessagingService service, Person person) {
		return dao.getMessagesToOrFromPersonUsingService(service,person);
	}

	public List<Message> findMessages(String content) {
		return dao.findMessages(content);
	}
	
	public List<Message> findMessagesWithAdresses(MessagingService service, String toAddress, String fromAddress, String content, Integer status) {
		return dao.findMessagesWithAddresses(service,toAddress,fromAddress,content,status);
	}


	public List<Message> findMessagesWithPeople(MessagingService service, Person sender, Person recipient, String content, Integer status) {
		return dao.findMessagesWithPeople(service, sender, recipient, content, status);
	}

	public void deleteMessage(Message message) throws APIException {
		dao.deleteMessage(message);
	}

	public void saveMessage(Message message) throws APIException {
		dao.saveMessage(message);
	}
}
