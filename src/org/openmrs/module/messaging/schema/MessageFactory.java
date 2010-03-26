package org.openmrs.module.messaging.schema;


/**
 * @author Dieterich
 *
 * @param <M>
 */
public interface MessageFactory<M extends Message, A extends MessagingAddress> {
	
	public M createMessage(String content, A origin, A destination);
	
	public M createMessageFromCurrentUser(String content, A destination);
		
	public boolean messageContentIsValid(String content);
}
