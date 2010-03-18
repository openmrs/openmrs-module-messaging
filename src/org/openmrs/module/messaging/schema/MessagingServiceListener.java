package org.openmrs.module.messaging.schema;

/**
 * An interface for objects that want to be notified when a message is received
 * by a messaging service. MessageServiceListeners can either listen on a
 * MessagingService or across messaging services by registering to listen on the
 * MessagingCenter.
 * 
 * @param <M> The message type that this listener will receive notifications on
 */
public interface MessagingServiceListener<M extends Message> {
	
	/**
	 * This method is called when a MessagingService receives a message
	 * @param message The message that was received
	 */
	public void messageRecieved(M message);
}
