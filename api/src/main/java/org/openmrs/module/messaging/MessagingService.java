package org.openmrs.module.messaging;

import java.util.List;
import java.util.Set;

import org.openmrs.Person;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.domain.listener.IncomingMessageListener;

/**
 * The MessagingService provides methods for sending messages and interacting
 * with other parts of the messaging framework.
 * 
 */
public interface MessagingService {

	/**
	 * A basic method for sending messages. Specify the message, destination,
	 * and protocol, and the messaging module will take care of the rest. If you
	 * provide an invalid message, address, or protocol, then an exception will
	 * be thrown. <br/>
	 * <br/>
	 * If the address that you provide is owned by a person in OpenMRS, then the
	 * message will be recorded as being 'to' that person. Otherwise the
	 * {@link Message#recipient} field of the message will remain null. To set
	 * the recipient of a message yourself, use the
	 * {@link #sendMessage(Message)} method after constructing your own Message
	 * object.
	 * 
	 * @param message
	 *            The content of the message to be sent
	 * @param address
	 *            The destination of the message
	 * @param protocolClass
	 *            The protocol that the message uses
	 * @throws Exception
	 */
	public void sendMessage(String message, String address, Class<? extends Protocol> protocolClass) throws Exception;

	/**
	 * Sends a message. Create your own message object using one of the
	 * {@link Protocol#createMessage(String)} methods, and then pass it to this
	 * method. If you do not use the {@link Protocol} as a factory for creating
	 * messages and instead create your own then you may end up with invalid
	 * messages, which will cause errors.
	 * 
	 * @param message
	 */
	public void sendMessage(Message message)  throws Exception;

	/**
	 * Sends a message to the provided person using their preferred address.
	 * TODO: error handling
	 * 
	 * @param message
	 * @param person
	 */
	public void sendMessageToPreferredAddress(String message, Person person);

	/**
	 * Queue multiple messages with this method.
	 * 
	 * @param messages
	 */
	public void sendMessages(Set<Message> messages)  throws Exception;

	/**
	 * @return All protocols
	 */
	public List<Protocol> getProtocols();
	
	/**
	 * Returns the protocol of the provided class. If there is no protocol of
	 * that class, null is returned.
	 * 
	 * @param clazz
	 * @return the protocol or null
	 */
	public <P extends Protocol> P getProtocolByClass(Class<P> clazz);
	
	public Protocol getProtocolByAbbreviation(String abbrev);

	/**
	 * Checks to see if there is at least one active gateway that can carry this
	 * protocol.
	 * 
	 * @param p
	 * @return
	 */
	public boolean canSendToProtocol(Class <? extends Protocol> protocolClass);

	/**
	 * Adds a message listener. Currently the only supported listeners are
	 * incoming message listeners that are notified when a message is received.
	 * 
	 * @param listener
	 */
	public void registerListener(IncomingMessageListener listener);

	/**
	 * Removes a message listener
	 * 
	 * @param listener
	 */
	public void unregisterListener(IncomingMessageListener listener);

	/**
	 * Notifies all message listeners of an event. Currently, this method calls
	 * the messageReceived method on all IncomingMessageListeners. The
	 * functionality of this method will change as new listener types are
	 * introduced. This method should generally NOT be called by other modules,
	 * and is for messaging module internal use only.
	 * 
	 * @param message
	 *            The message that was received
	 */
	public void notifyListeners(Message message);
	
	/**
	 * This method should never be used outside of the messaging module.
	 * @return the Gateway Manager
	 */
	public GatewayManager getGatewayManager();
	
	/**
	 * For use by Spring only
	 * @param gatewayManager
	 */
	public void setGatewayManager(GatewayManager gatewayManager);

}
