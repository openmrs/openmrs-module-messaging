package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.sms.SmsProtocol;
import org.openmrs.module.messaging.twitter.TwitterProtocol;

/**
 * The implementation of the MessagingService interface 
 */
public class MessagingServiceImpl extends BaseOpenmrsService implements MessagingService {
	
	private static Log log = LogFactory.getLog(MessagingServiceImpl.class);
			
	/**
	 * A map containing all the protocols that this system can use
	 */
	private Map<Class<? extends Protocol>, Protocol> protocols;
		
	/**
	 * A thread-safe ArrayList of all the message listeners
	 */
	private CopyOnWriteArrayList<IncomingMessageListener> listeners;
	
	/**
	 * Sets up the Messaging Service by creating all the protocols
	 */
	public MessagingServiceImpl(){
		listeners = new CopyOnWriteArrayList<IncomingMessageListener>();
		//initialize the protocols
		protocols = new HashMap<Class<? extends Protocol>, Protocol>();
		protocols.put(SmsProtocol.class, new SmsProtocol());
		protocols.put(TwitterProtocol.class, new TwitterProtocol());
	}
	
	public void sendMessage(String message, String address, Class<? extends Protocol> protocolClass) throws Exception{
		Protocol p = getProtocolByClass(protocolClass);
		if(p == null) throw new Exception("Invalid protocol class");
		Message m = p.createMessage(message, address, null);
		sendMessage(m);
	}
	
	public void sendMessage(Message message){
		message.setMessageStatus(MessageStatus.OUTBOX);
		Context.getService(MessageService.class).saveMessage(message);
	}
	
	public void sendMessages(Set<Message> messages){
		for(Message m: messages){
			sendMessage(m);
		}
	}
	
	public void sendMessageToPreferredAddress(String message, Person person){
		//TODO: Make this work
	}

	
	public List<Protocol> getProtocols(){
		return new ArrayList<Protocol>(protocols.values());
	}
	
	public List<Protocol> getActiveProtocols(){
		List<Protocol> results = new ArrayList<Protocol>();
		for(Protocol p: protocols.values()){
			if(canSendToProtocol(p)){
				results.add(p);
			}
		}
		return results;
	}
	
	public <P extends Protocol> P getProtocolByClass(Class<? extends P> clazz) {
		return (P) protocols.get(clazz);
	}
	
	public Protocol getProtocolById(String protocolId){
		Protocol result = null;
		for(Protocol p: protocols.values()){
			if(p.getProtocolId().equals(protocolId)){
				result = p;
			}
		}
		return result;
	}
	
	public boolean canSendToProtocol(Protocol p){
		if(MessagingModuleActivator.manager == null) return false;
		return MessagingModuleActivator.manager.getActiveSupportingGateways(p).size() > 0;
	}
	

	public void registerListener(IncomingMessageListener listener){
		listeners.addIfAbsent(listener);
	}
	
	public void unregisterListener(IncomingMessageListener listener){
		listeners.remove(listener);
	}
	
	public void notifyListeners(Message message){
		for(IncomingMessageListener listener: listeners){
			listener.messageRecieved(message);
		}
	}
	
}
