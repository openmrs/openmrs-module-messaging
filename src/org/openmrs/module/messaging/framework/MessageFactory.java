package org.openmrs.module.messaging.framework;


/**
 * @author Dieterich
 *
 * @param <M>
 */
public interface MessageFactory<M extends Message> {
	
	public M createMessageFromCurrentUser(String content, String destination);
	
	public M createMessageWithPriority(String content, String origin, String destination, Integer priority);
	
	public M createMessageFromCurrentUserWithPriority(String content, String destination, Integer priority);
	
	
}
