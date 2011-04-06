package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;

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
	 * @see MessageService#findMessagesWithAdresses(MessageService, String, String, String, Integer)
	 */
	public List<Message> findMessagesWithAddresses(Class<? extends Protocol> protocolClass, String toAddress,String fromAddress, String content,Integer status);

	/**
	 * @see MessageService#findMessages(MessagingGateway, Person, Person, String, Integer)
	 */
	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocolClass, Person sender, Person recipient, String content, Integer status);

	/**
	 * @see MessageService#saveMessage(Message)
	 */
	public void saveMessage(Message message) throws APIException;

	/**
	 * @see MessageService#deleteMessage(Message)
	 */
	public void deleteMessage(Message message) throws APIException;

	public List<Message> getOutboxMessages();

	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocolClass);
	
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to, boolean dateOrderAscending);
	
	public Integer countMessagesForPerson(int personId, boolean to);
}
