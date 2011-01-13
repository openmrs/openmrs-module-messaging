package org.openmrs.module.messaging.domain.listener;

import org.openmrs.module.messaging.domain.Message;

/**
 * An interface for objects that want to be notified when a message is received
 * by the messaging service.
 */
public interface IncomingMessageListener {
	
	/**
	 * This method is called when a MessagingGateway receives a message
	 * @param message The message that was received
	 */
	public void messageRecieved(Message message);
}
