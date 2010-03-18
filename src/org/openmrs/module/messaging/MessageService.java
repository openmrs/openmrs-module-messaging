package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageAddress;
import org.openmrs.module.messaging.schema.MessagingService;

public interface MessageService {

	public List<Message> getAllMessages();
	
	public List<Message> getMessagesFromPerson(Person sender);
	
	public List<Message> getMessagesToPerson(Person recipient);
	
	public List<Message> getMessagesFromAddress(MessageAddress address);
	
	public List<Message> getMessagesToAddress(MessageAddress address);
	
	public List<Message> getMessagesForService(MessagingService service);
	
	public List<Message> getMessagesToPersonUsingService(MessagingService service, Person recipient);
	
	public List<Message> getMessagesFromPersonUsingService(MessagingService service, Person sender);
	
	public List<Message> getMessages(MessagingService service, Person sender, Person recipient, Integer status, String content);
	
}
