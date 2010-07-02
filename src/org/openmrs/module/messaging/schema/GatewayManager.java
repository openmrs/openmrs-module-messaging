package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.sms.SmsLibGateway;

public class GatewayManager extends Thread {

	/** All the messaging gateways that this manager manages*/
	private List<MessagingGateway> gateways;
	
	private MessageService messageService;
	
	private boolean isStarted=false;
	
	public GatewayManager(){
		messageService = Context.getService(MessageService.class);
		gateways = new ArrayList<MessagingGateway>();
		//add the gateways
		gateways.add(new SmsLibGateway());
	}
	/**
	 * Stop the the manager
	 */
	public void halt(){
		isStarted = false;
	}
	
	private void startGateways(){
		for(MessagingGateway mg: gateways){
			mg.startup();
		}
	}
	
	public void run(){
		startGateways();
		isStarted=true;
		while(isStarted){
			dispatchOutgoingMessages();
		}
	}
	
	/**
	 * This method does the dispatching of outgoing messages.
	 * It pulls any messages from the database that have a status
	 * of 'outbox' and then dispatches them to the proper gateway.
	 * This method is separated out of it's containing while loop
	 * for readability and easier testing. 
	 */
	private void dispatchOutgoingMessages(){
		//organize outgoing messages by protocol
		for(Protocol p: MessagingService.getProtocols()){
			//get all outgoing messages and the gateways that can send them
			List<Message> messages = messageService.getOutboxMessagesByProtocol(p);
			List<MessagingGateway> gateways = getActiveSupportingGateways(p);
			int messageIndex = -1;
			int gatewayCount = gateways.size();
			for(Message message: messages){
				boolean messageSent =false;
				//give all the gateways the option to grab the message
				for(MessagingGateway gateway: gateways){
					if(gateway.shouldSendMessage(message)){
						gateway.sendMessage(message);
						messageSent=true;
						break;
					}
				}
				//if no gateway 'wants' the message, we let another gateway have it
				if(!messageSent){
					//round robin
					gateways.get(++messageIndex % gatewayCount).sendMessage(message);
				}
			}
		}
	}
	
	/**
	 * Returns all gateways that are active and support sending
	 * messages encoding in the supplied protocol
	 */
	public List<MessagingGateway> getActiveSupportingGateways(Protocol p){
		List<MessagingGateway> results = new ArrayList<MessagingGateway>();
		for(MessagingGateway mg: gateways){
			if(mg.canSend() && mg.isActive() && mg.supportsProtocol(p)){
				results.add(mg);
			}
		}
		return results;
	}
	
}
