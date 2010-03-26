package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingService;

/**
 * @author Dieterich
 *
 */
public interface MessageDAO {

	
	/**
	 * @see MessageService#getAllMessages()
	 */
	public List<Message> getAllMessages();
	
	/**
	 * @see MessageService#getMessage(Integer)
	 */
	public Message getMessage(Integer messageId);
	
	/**
	 * @see MessageService#getMessagesFromPerson(Person)
	 */
	public List<Message> getMessagesFromPerson(Person sender);
	
	/**
	 * @see MessageService#getMessagesToPerson(Person)
	 */
	public List<Message> getMessagesToPerson(Person recipient);
	
	/**
	 * @see MessageService#getMessagesToOrFromPerson(Person)
	 */
	public List<Message> getMessagesToOrFromPerson(Person person);
	
	/**
	 * @see MessageService#getMessagesFromAddress(String)
	 */
	public List<Message> getMessagesFromAddress(String address);
	
	/**
	 * @see MessageService#getMessagesToAddress(String)
	 */
	public List<Message> getMessagesToAddress(String address);
	
	/**
	 * @see MessageService#getMessagesToOrFromAddress(String)
	 */
	public List<Message> getMessagesToOrFromAddress(String address);
	
	/**
	 * @see MessageService#getMessagesForService(MessagingService)
	 */
	public List<Message> getMessagesForService(MessagingService service);
	
	/**
	 * @see MessageService#getMessagesToPersonUsingService(MessagingService, Person)
	 */
	public List<Message> getMessagesToPersonUsingService(MessagingService service, Person recipient);

	/**
	 * @see MessageService#getMessagesFromPersonUsingService(MessagingService, Person)
	 */
	public List<Message> getMessagesFromPersonUsingService(MessagingService service, Person sender);

	/**
	 * @see MessageService#getMessagesToOrFromPersonUsingService(MessagingService, Person)
	 */
	public List<Message> getMessagesToOrFromPersonUsingService(MessagingService service, Person person);

	/**
	 * @see MessageService#findMessages(String)
	 */
	public List<Message> findMessages(String content);

	/**
	 * @see MessageService#findMessagesWithAdresses(MessageService, String, String, String, Integer)
	 */
	public List<Message> findMessagesWithAddresses(MessagingService service, String toAddress,String fromAddress, String content,Integer status);

	/**
	 * @see MessageService#findMessages(MessagingService, Person, Person, String, Integer)
	 */
	public List<Message> findMessagesWithPeople(MessagingService service, Person sender, Person recipient, String content, Integer status);

	/**
	 * @see MessageService#saveMessage(Message)
	 */
	public void saveMessage(Message message) throws APIException;

	/**
	 * @see MessageService#deleteMessage(Message)
	 */
	public void deleteMessage(Message message) throws APIException;
}
