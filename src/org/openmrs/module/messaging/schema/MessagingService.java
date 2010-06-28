package org.openmrs.module.messaging.schema;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;

/**
 * The Messaging Service is the main class in the Messaging framework. It is
 * focused on cross-service functionality like sending to preferred messaging
 * addresses and listening across all messaging gateways.
 * 
 * @author Dieterich
 * 
 */
public class MessagingService {
	
	protected static Log log = LogFactory.getLog(MessagingService.class);
	
	protected static MessagingService instance;
	
	protected Set<MessagingGateway> gateways;
		
	public void initGateways() {
		for(MessagingGateway ms:gateways){
				ms.startup();
		}
	}
	
	public MessagingService(){}
	
	/**
	 * TEMPORARY METHOD
	 * This method returns the singleton instance of MessagingService.
	 * Once the module startup() method is changed to be after the spring
	 * applicationcontext is already initialized, we will move back to spring
	 * dependency injection and bean management
	 * @return
	 */
	public static MessagingService getInstance(){
		if(instance != null){
			return instance;
		}else{
			instance = new MessagingService();
			instance.setup();
			return instance;
		}
	}
	public void sendMessage(String message, String address, Protocol p){}
	
	public void sendMessageToPreferredAddress(String message, Person person){}
	
	public void setup(){
		gateways = new HashSet<MessagingGateway>();
		//gateways.add(new TwitterGateway(this));
	}

	public void registerListenerForPerson(MessagingServiceListener listener, Person person) {}
	
	public void registerListenerForProtocol(MessagingServiceListener listener, Protocol protocol ) {}
	
	public void registerListenerForGateway(MessagingServiceListener listener, MessagingGateway gateway) {}

	public void registerListener(MessagingServiceListener listener) {}

	//> service getter methods

	/**
	 * @return All messaging gateways
	 */
	public Set<MessagingGateway> getAllMessagingGateways() {
		return gateways;
	}
	
	/**
	 * @return All Messaging Gateways that can send messages
	 */
	public Set<MessagingGateway> getActiveMessagingGateways(){
		Set<MessagingGateway> gateways = new HashSet<MessagingGateway>();
		for(MessagingGateway msg: gateways){
			if(msg.canSend()){
				gateways.add(msg);
			}
		}
		return gateways;
	}
	
	public Set<MessagingGateway> getSupportingGateways(Protocol p){
		Set<MessagingGateway> gateways = new HashSet<MessagingGateway>();
		for(MessagingGateway msg: gateways){
			if(msg.supportsProtocol(p)){
				gateways.add(msg);
			}
		}
		return gateways;
	}
	
	public Set<MessagingGateway> getActiveSupportingGateways(Protocol p){
		Set<MessagingGateway> gateways = new HashSet<MessagingGateway>();
		for(MessagingGateway msg: gateways){
			if(msg.supportsProtocol(p) && msg.canSend()){
				gateways.add(msg);
			}
		}
		return gateways;
	}
	
}
