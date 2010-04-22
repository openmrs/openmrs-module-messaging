package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.springframework.transaction.annotation.Transactional;

public interface MessageService extends OpenmrsService{

	/**
	 * @return all messages
	 */
	@Transactional(readOnly=true)
	public List<Message> getAllMessages();
	
	/**
	 * @param messageId
	 * @return the message with the corresponding messageId
	 */
	@Transactional(readOnly=true)
	public Message getMessage(Integer messageId);
	
	/**
	 * @param sender
	 * @return all messages that sender has sent
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesFromPerson(Person sender);
	
	/**
	 * @param recipient
	 * @return all messages that recipient has received
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToPerson(Person recipient);
	
	/**
	 * @param person
	 * @return all messages to or from person
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToOrFromPerson(Person person);
	
	/**
	 * @param address
	 * @return all messages sent from the supplied address 
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesFromAddress(String address);
	
	/**
	 * @param address
	 * @return all messages sent to the supplied address 
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToAddress(String address);
	
	/**
	 * @param address
	 * @return all messages sent to or from the supplied address 
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToOrFromAddress(String address);
	
	/**
	 * Returns all messages of the message type that is handled
	 * by the provided gateway.
	 * @param gateway
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesForGateway(MessagingGateway gateway);
	
	/**
	 * Returns all messages of the message type that is handled
	 * by the provided gateway that were sent to "recipient"
	 * @param gateway
	 * @param recipient
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToPersonUsingGateway(MessagingGateway gateway, Person recipient);
	
	/**
	 * Returns all messages of the message type that is handled
	 * by the provided gateway that were sent by "sender"
	 * @param gateway
	 * @param sender
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesFromPersonUsingGateway(MessagingGateway gateway, Person sender);
	
	/**
	 * Returns all messages that were sent to or from
	 * 'person' by the supplied gateway
	 * @param gateway
	 * @param person
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> getMessagesToOrFromPersonUsingGateway(MessagingGateway gateway, Person person);
	
	/**
	 * Performs a like query on the Message.content field
	 * using wildcards on each side of the search text.
	 * @param content
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> findMessages(String content);
	
	/**
	 * Any of the parameters can be null
	 * @param gateway the gateway that handles the message type that you want
	 * @param toAddress the address that the message was sent to
	 * @param fromAddress the address that the message was sent from
	 * @param content the content of the message (performs a like query)
	 * @param status the status of the message
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> findMessagesWithAdresses(MessagingGateway gateway, String toAddress,String fromAddress, String content,Integer status);
	
	/**
	 * Any of the parameters can be null
	 * @param gateway the gateway that handles the message type that you want
	 * @param sender the person that the message was sent to
	 * @param recipient the person that the message was sent by
	 * @param content the content of the message (performs a like query)
	 * @param status the status of the message
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Message> findMessagesWithPeople(MessagingGateway gateway, Person sender, Person recipient, String content, Integer status);
	
	/**
	 * Create or update message
	 */
	@Transactional
	public void saveMessage(Message message) throws APIException;

	/**
	 * Delete message
	 */
	@Transactional
	public void deleteMessage(Message message) throws APIException;

}
