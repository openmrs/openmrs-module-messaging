package org.openmrs.module.messaging.schema;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.util.ReflectionUtils;

/**
 * An abstract superclass that represents a service that can send and receive
 * messages. All methods in this class should be thread safe since it will be
 * used as a singleton, and could potentially be accessed by multiple threads.
 * 
 * @param <M>
 *            The type of messages that this service handles
 */
public abstract class MessagingService<M extends Message, A extends MessagingAddress> {

	protected CopyOnWriteArrayList<MessagingServiceListener> listeners;

	/**
	 * Sends a message to the address specified with the content specified.
	 * Depending on the implementation, this method may throw exceptions due to
	 * improperly formatted addresses or messages.
	 * 
	 * @param address
	 * @param content
	 */
	public abstract void sendMessage(String address, String content);

	/**
	 * Sends a message to the destination specified in
	 * {@link Message#destination}. This method should handle the setting of the
	 * {@link Message#dateSent}, {@link Message#dateReceived} , and
	 * {@link Message#origin} fields of {@link Message} if it is applicable to
	 * that message. Implementations of this method need to be thread safe, and
	 * should honor the {@link Message#priority} value if applicable to this
	 * messaging service.
	 * 
	 * @param message
	 */
	public abstract void sendMessage(M message);

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
	public abstract void sendMessage(M message, MessageDelegate delegate);

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
	public abstract void sendMessages(List<M> messages, MessageDelegate delegate);

	/**
	 * Sends one message to multiple addresses. Implementations of this method
	 * need to handle the setting of {@link Message#dateSent},
	 * {@link Message#dateReceived}, {@link Message#destination}, and
	 * {@link Message#origin} when/if the messages are saved to the database.
	 * Additionally, this method should be thread safe, tolerate a null
	 * {@link MessageDelegate}, and honor the {@link Message#priority} if
	 * applicable to this messaging service.
	 * 
	 * @param m
	 * @param addresses
	 */
	public abstract void sendMessageToAddresses(M m, List<String> addresses,
			MessageDelegate delegate);

	/**
	 * Registers a listener to receive notifications when a message is received
	 * by this service. This method is thread safe.
	 * 
	 * @param listener
	 *            The listener to register
	 */
	public void registerListener(MessagingServiceListener listener) {
		listeners.addIfAbsent(listener);
	}

	/**
	 * Unregisters a listener that was registered. This method is thread safe.
	 * 
	 * @param listener
	 *            The listener to unregister
	 */
	public void unregisterListener(MessagingServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Should return the default sender address of this messaging service. This
	 * would most likely be the address from which OpenMRS sends messages.
	 * 
	 * @return the default address
	 */
	public abstract A getDefaultSenderAddress();

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

	public abstract void startup();

	public abstract void shutdown();
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public abstract AddressFactory getAddressFactory();
	
	public abstract MessageFactory getMessageFactory();

	/**
	 * Returns the class of the messages that this service handles
	 * 
	 * @return
	 */
	public Class<?> getMessageClass() {
		List<Class<?>> genericParameters = ReflectionUtils.getTypeArguments(
				MessagingService.class, getClass());
		for (Class<?> c : genericParameters) {
			if (ReflectionUtils.classExtendsClass(c, Message.class)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Returns the class of the messaging addresses that this service handles
	 * 
	 * @return
	 */
	public Class<?> getMessagingAddressClass() {
		List<Class<?>> genericParameters = ReflectionUtils.getTypeArguments(
				MessagingService.class, getClass());
		for (Class<?> c : genericParameters) {
			if (ReflectionUtils.classExtendsClass(c, MessagingAddress.class)) {
				return c;
			}
		}
		return null;
	}
	
	protected void saveMessage(M message){
		message.setDateSent(new Date());
		((MessageService) Context.getService(MessageService.class)).saveMessage(message);

	}

}
