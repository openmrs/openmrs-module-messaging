package org.openmrs.module.messaging.framework;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An abstract superclass that represents a service that can send and receive
 * messages. All methods in this class should be thread safe since it will be
 * used as a singleton, and could potentially be accessed by multiple threads.
 * 
 * @param <E>
 *            The type of messages that this service handles
 */
public abstract class MessagingService<E extends Message> {

	protected CopyOnWriteArrayList<MessagingServiceListener> listeners;

	/**
	 * Sends a message to the destination specified. This method should handle
	 * the setting of the {@link Message#dateSent}, {@link Message#dateRecieved}
	 * , and {@link Message#origin} fields of {@link Message} if it is
	 * applicable to that message. Implementations of this method need to be
	 * thread safe
	 * 
	 * @param message
	 * @param destination
	 */
	public abstract void sendMessage(E message, String destination);

	/**
	 * Sends a message to the destination specified. This method should handle
	 * the setting of the {@link Message#dateSent}, {@link Message#dateRecieved}
	 * , and {@link Message#origin} fields of {@link Message} if it is
	 * applicable to that message. The delegate provided will receive the
	 * callbacks specified in the {@link MessageDelegate} interface.
	 * Implementations of this method need to be thread safe.
	 * 
	 * @param message
	 *            The message to be sent
	 * @param destination
	 *            The destination for the message to be sent to
	 * @param delegate
	 *            The delegate that will receive callbacks
	 */
	public abstract void sendMessage(E message, String destination,
			MessageDelegate delegate);

	/**
	 * Sends a collection of messages (the keys in the map) to the destinations
	 * specified (the values in the map). This method should handle the setting
	 * of the {@link Message#dateSent}, {@link Message#dateRecieved}, and
	 * {@link Message#origin} fields of {@link Message} if it is applicable to
	 * that message. The delegate provided will receive the callbacks specified
	 * in the {@link MessageDelegate} interface. Implementations of this message
	 * need to be thread safe.
	 * 
	 * @param messages
	 *            A map of to-be-sent messages to the addresses that they should
	 *            be sent to
	 * @param delegate
	 *            The delegate that will receive callbacks
	 */
	public abstract void sendMessages(Map<E, String> messages,
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
	 * Should return the default sender address of this messaging service. This would
	 * most likely be the address from which OpenMRS sends messages.
	 * @return the default address
	 */
	public abstract Address getDefaultSenderAddress();
	
	/**
	 * Should return true if the messaging service has the ability
	 * to send messages currently and return false otherwise.
	 * @return
	 */
	public abstract boolean canSend();
	
	/**
	 * Should return true if the messaging service has the ability
	 * to receive messages currently and return false otherwise.
	 * @return
	 */
	public abstract boolean canReceive();

}
