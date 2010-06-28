package org.openmrs.module.messaging.schema;

import java.util.List;

import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;

/**
 * An abstract superclass that represents a service that can send and receive
 * messages.
 */
public abstract class MessagingGateway {

	protected MessagingAddressService addressService;
	
	protected MessageService messageService;
	
	/**
	 * Sends a message to the address specified with the content specified.
	 * Depending on the implementation, this method may throw exceptions due to
	 * improperly formatted addresses or messages.
	 * 
	 * @param address
	 * @param content
	 */
	public abstract void sendMessage(String address, String content, Protocol p)
			throws Exception;

	/**
	 * Sends a message to the destination specified in
	 * {@link Message#destination}. <br>
	 * </br> Messages passed in to this address should have their destination,
	 * origin, and This method should handle setting the {@link Message#origin}
	 * of the {@link Message} if it is applicable to that message (like if the
	 * message is being sent from the gateway default address). <br>
	 * </br>Both person fields ({@link Message#sender} and
	 * {@link Message#recipient}) of the message will be filled in by the 'save'
	 * method, but will be left alone if they are already set. <br>
	 * </br>Implementations of this method need to be thread safe, and should
	 * honor the {@link Message#priority} value if applicable to this messaging
	 * service.
	 * 
	 * @param message
	 */
	public abstract void sendMessage(Message message) throws Exception;

	/**
	 * Sends a message to the destination specified in
	 * {@link Message#destination}. This method should handle the setting of the
	 * {@link Message#dateSent}, {@link Message#dateReceived} , and
	 * {@link Message#origin} fields of {@link Message} if it is applicable to
	 * that message. The delegate provided will receive the callbacks specified
	 * in the {@link MessageDelegate} interface. Implementations of this method
	 * need to be thread safe, allow a null {@link MessageDelegate}, and honor
	 * the {@link Message#priority} value if applicable to this messaging
	 * service
	 * 
	 * @param message
	 *            The message to be sent
	 * @param delegate
	 *            The delegate that will receive callbacks. This can be null.
	 */
	public abstract void sendMessage(Message message, MessageDelegate delegate);

	/**
	 * Sends a collection of messages to the destinations specified in
	 * {@link Message#destination}. This method should handle the setting of the
	 * {@link Message#dateSent}, {@link Message#dateReceived}, and
	 * {@link Message#origin} fields of the {@link Message} if it is applicable
	 * to that message. The delegate provided will receive the callbacks
	 * specified in the {@link MessageDelegate} interface. Implementations of
	 * this method need to be thread safe, allow a null {@link MessageDelegate},
	 * and honor the {@link Message#priority} if applicable to this messaging
	 * service.
	 * 
	 * @param messages
	 *            The messages to be sent
	 * @param delegate
	 *            The delegate that will receive callbacks. This can be null.
	 */
	public abstract void sendMessages(List<Message> messages, MessageDelegate delegate);

	/**
	 * Should return the default sender address of this messaging service. This
	 * would most likely be the address from which OpenMRS "sends" messages like
	 * an official twitter feed.
	 * 
	 * @return the default address
	 */
	public abstract MessagingAddress getDefaultSenderAddress();

	/**
	 * Should return true if the messaging service has the ability to send
	 * messages currently and return false otherwise.
	 * 
	 * @return
	 */
	public abstract boolean canSend();

	/**
	 * Should return true if the messaging service has the ability to receive
	 * messages currently and return false otherwise.
	 * 
	 * @return
	 */
	public abstract boolean canReceive();

	/**
	 * Should perform all necessary operations to start the Gateway so that
	 * either canSend or canReceive returns true (ideally both)
	 */
	public abstract void startup();

	/**
	 * Should perform all necessary operations to stop the Gateway. Both
	 * canRecieve and canSend should return false.
	 */
	public abstract void shutdown();

	/**
	 * Should return the display name of this messaging gateway. This name will
	 * be used in UI and should be internationalized
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Should return a short description of the messaging gateway
	 * 
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Should return a unique id for this messaging gateway. Should also be
	 * as short as possible, no spaces
	 * 
	 * @return
	 */
	public abstract String getGatewayId();

	/**
	 * Should return a boolean representing whether or not this gateway can send
	 * messages from user addresses for the given protocol. Would return false if all messages
	 * must be routed from a single origin, the default sender address. An example of this would be 
	 * an SMS modem connected to the OpenMRS server - it's impossible to send SMS's from user's
	 * phone numbers 
	 */
	public abstract boolean canSendFromUserAddresses(Protocol protocol);

	/**
	 * Should return true if this gateway supports sending messages using the
	 * provided protocol. With this method it is possible for gateways to
	 * support more than one protocol at once.
	 * 
	 * @param protocol
	 * @return
	 */
	public abstract boolean supportsProtocol(Protocol protocol);
	
	protected void saveMessage(Message m){
		// - set message sender if not set and can find an address for it
		// - set message recipient if not set and can find an address for it
		// - set date sent
		// - save
	}

}
