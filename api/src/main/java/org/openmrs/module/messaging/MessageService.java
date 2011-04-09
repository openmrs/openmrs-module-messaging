package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service for database-related message tasks. To send messages, use the
 * {@link MessagingService}, not this service.
 * 
 * @author dieterichlawson
 */
public interface MessageService extends OpenmrsService {

	/**
	 * @return all messages
	 * @should return all messages
	 */
	@Transactional(readOnly = true)
	public List<Message> getAllMessages();

	/**
	 * @param messageId
	 * @return the message with the corresponding messageId
	 * @should return message with given id
	 */
	@Transactional(readOnly = true)
	public Message getMessage(Integer messageId);

	/**
	 * @param person
	 *            the person related to the messages
	 * @param to
	 *            boolean signaling whether the returned messages should be to
	 *            or from the person
	 * @return all messages to or from the person
	 * @should return all messages from person if to is false
	 * @should return all messages to person if to is true
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesForPerson(Person person, boolean to);

	/**
	 * @param person
	 * @return all messages to or from person
	 * @should return all messages to or from person
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesToOrFromPerson(Person person);

	/**
	 * @param address
	 *            the address related to the messages
	 * @param to
	 *            boolean signaling whether the returned messages should be to
	 *            or from the address
	 * @return all messages to or from the supplied address
	 * @should return all messages from address if to is false
	 * @should return all messages to address if to is true
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesForAddress(String address, boolean to);

	/**
	 * @param address
	 * @return all messages sent to or from the supplied address
	 * @should return all messages to or from address
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesToOrFromAddress(String address);

	/**
	 * Performs a like query on the Message.content field using wildcards on
	 * each side of the search text.
	 * 
	 * @param content the string to search for (no regexes)
	 * @should return all messages with supplied string in message content
	 */
	@Transactional(readOnly = true)
	public List<Message> findMessages(String content);

	/**
	 * Any of the parameters can be null
	 * 
	 * @param protocol
	 *            the protocol of the message
	 * @param toAddress
	 *            the address that the message was sent to
	 * @param fromAddress
	 *            the address that the message was sent from
	 * @param content
	 *            the content of the message (performs a like query)
	 * @param status
	 *            the status of the message
	 * @return
	 * @should perform an OR query when to and from addresses are present
	 */
	@Transactional(readOnly = true)
	public List<Message> findMessagesWithAdresses(Class<? extends Protocol> protocol, String toAddress, String fromAddress, String content, Integer status);

	/**
	 * Any of the parameters can be null
	 * 
	 * @param protocol
	 *            the protocol of the message
	 * @param sender
	 *            the person that the message was sent to
	 * @param recipient
	 *            the person that the message was sent by
	 * @param content
	 *            the content of the message (performs a like query)
	 * @param status
	 *            the status of the message
	 * @return
	 * @should perform an OR query when to and from people are present
	 */
	@Transactional(readOnly = true)
	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocol, Person sender, Person recipient, String content, Integer status);

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

	/**
	 * @return all messages with a status of outbox
	 * @should return all messages with status of outbox
	 */
	@Transactional(readOnly = true)
	public List<Message> getOutboxMessages();

	/**
	 * @return all messages with a status of outbox
	 * @should return all outbox messages for protocol
	 */
	@Transactional(readOnly = true)
	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocol);

	/**
	 * @param protocol
	 * @param status
	 * @return all messages with the supplied status and protocol
	 * @should return all messages with supplied status and protocol
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesForProtocolAndStatus(Class<? extends Protocol> protocol, Integer status);

	/**
	 * @param pageNumber
	 * @param pageSize
	 * @param personId
	 * @param to
	 * @param dateOrderAscending
	 * @return messages to or from a person, paged
	 * @should return messages to a person if to is true
	 * @should return messages from a person if to is false
	 * @should not return more than pageSize messages
	 * @should return arbitrary pages of data
	 * @should return in descending order by date if dateOrderAscending is false
	 * @should return in ascending order by date if dateOrderAscending is true
	 */
	@Transactional(readOnly = true)
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to, boolean dateOrderAscending, Class<? extends Protocol> protocolClass);

	/**
	 * @param personId the Id of the person
	 * @param to boolean signaling if the method should count messages to the person or from the person
	 * @return the number of messages to or from a person
	 * @should return number of messages to or from a person
	 */
	@Transactional(readOnly = true)
	public Integer countMessagesForPerson(int personId, boolean to);
	
	@Transactional(readOnly=true)
	public List<Message> searchMessages(int pageNumber, int pageSize, String searchString, Person p, boolean inbox, boolean outbox);

	public Integer countSearch(Person person, String searchString,boolean inbox, boolean outbox);

}
