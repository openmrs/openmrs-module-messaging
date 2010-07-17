package org.openmrs.module.messaging.schema;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.sms.SmsProtocol;
import org.openmrs.module.messaging.twitter.TwitterProtocol;

/**
 * The Messaging Service is the main class in the Messaging framework. It is
 * focused on cross-service functionality like sending to preferred messaging
 * addresses and listening across all messaging gateways.
 * 
 * @author Dieterich
 * 
 */
public class MessagingService {
	
	private static Log log = LogFactory.getLog(MessagingService.class);
	
	private static MessagingService instance;
	
	private MessageService messageService;
	
	private static Set<Protocol> protocols;
	
	private GatewayManager manager;
	
	static{
		protocols = new HashSet<Protocol>();
		protocols.add(new SmsProtocol());
		protocols.add(new TwitterProtocol());
	}
	
	public MessagingService(){
		this.messageService = Context.getService(MessageService.class);
		manager = new GatewayManager();
		manager.start();
	}
	
	/**
	 * TEMPORARY METHOD
	 * This method returns the singleton instance of MessagingService.
	 * Once the module startup() method is changed to be after the spring
	 * application context is already initialized, we will move back to spring
	 * dependency injection and bean management
	 * @return
	 */
	public static MessagingService getInstance(){
		if(instance != null){
			return instance;
		}else{
			instance = new MessagingService();
			return instance;
		}
	}
	
	public void sendMessage(String message, String address, Protocol p) throws Exception{
		Message m = p.createMessage(message, address, null);
		sendMessage(m);
	}
	
	public void sendMessage(Message message){
		message.setMessageStatus(MessageStatus.OUTBOX);
		messageService.saveMessage(message);
	}
	
	public void sendMessages(Set<Message> messages){
		for(Message m: messages){
			sendMessage(m);
		}
	}
	
	public void sendMessageToPreferredAddress(String message, Person person){
		

	}
	
	public static Set<Protocol> getProtocols(){
		return protocols;
	}
	
	public static Protocol getProtocolById(String protocolId){
		Protocol result = null;
		for(Protocol p: protocols){
			if(p.getProtocolId().equals(protocolId)){
				result = p;
			}
		}
		return result;
	}
	
	public Set<Protocol> getActiveProtocols(){
		Set<Protocol> results = new HashSet<Protocol>();
		for(Protocol p: protocols){
			if(canSendToProtocol(p)){
				results.add(p);
			}
		}
		return results;
	}
	
	public boolean canSendToProtocol(Protocol p){
		return manager.getActiveSupportingGateways(p).size() > 0;
	}
}
