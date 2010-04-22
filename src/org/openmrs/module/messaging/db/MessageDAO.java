package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;

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
	 * @see MessageService#getMessagesForGateway(MessagingGateway)
	 */
	public List<Message> getMessagesForGateway(MessagingGateway gateway);
	
	/**
	 * @see MessageService#getMessagesToPersonUsingGateway(MessagingGateway, Person)
	 */
	public List<Message> getMessagesToPersonUsingGateway(MessagingGateway gateway, Person recipient);

	/**
	 * @see MessageService#getMessagesFromPersonUsingGateway(MessagingGateway, Person)
	 */
	public List<Message> getMessagesFromPersonUsingGateway(MessagingGateway gateway, Person sender);

	/**
	 * @see MessageService#getMessagesToOrFromPersonUsingGateway(MessagingGateway, Person)
	 */
	public List<Message> getMessagesToOrFromPersonUsingGateway(MessagingGateway gateway, Person person);

	/**
	 * @see MessageService#findMessages(String)
	 */
	public List<Message> findMessages(String content);

	/**
	 * @see MessageService#findMessagesWithAdresses(MessageService, String, String, String, Integer)
	 */
	public List<Message> findMessagesWithAddresses(MessagingGateway gateway, String toAddress,String fromAddress, String content,Integer status);

	/**
	 * @see MessageService#findMessages(MessagingGateway, Person, Person, String, Integer)
	 */
	public List<Message> findMessagesWithPeople(MessagingGateway gateway, Person sender, Person recipient, String content, Integer status);

	/**
	 * @see MessageService#saveMessage(Message)
	 */
	public void saveMessage(Message message) throws APIException;

	/**
	 * @see MessageService#deleteMessage(Message)
	 */
	public void deleteMessage(Message message) throws APIException;
}
