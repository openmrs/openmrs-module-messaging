package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.googlevoice.GoogleVoiceGateway;
import org.openmrs.module.messaging.twitter.TwitterGateway;

public class GatewayManager extends Thread {

	/** All the messaging gateways that this manager manages*/
	private static List<MessagingGateway> gateways;
	
	private MessageService messageService;
	
	private static Log log = LogFactory.getLog(GatewayManager.class);
	
	private int maxRetry;
	
	private boolean isStarted=false;
	
	private static final Random rand = new Random();
	
	public GatewayManager(){
		messageService = Context.getService(MessageService.class);
		gateways = new ArrayList<MessagingGateway>();
		//add the gateways
		//gateways.add(new SmsLibGateway());
		gateways.add(new TwitterGateway());
		gateways.add(new GoogleVoiceGateway());
		//get the max number of retries
		try{
			Context.openSession();
			maxRetry = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_MAX_RETRIES));
			Context.closeSession();
		}catch(Exception e){
			maxRetry = 3;
		}
	}
	
	/**
	 * Stop the the manager
	 */
	public void halt(){
		isStarted = false;
	}
	
	/**
	 * Starts all the gateways
	 */
	private void startGateways(){
		for(MessagingGateway mg: gateways){
			mg.startup();
			log.info("Gateway:\""+mg.getName()+"\" started up");
		}
	}
	
	/**
	 * This method is called to run the thread. It starts the gateways and
	 * then enters into a loop where it dispatches outgoing messages and then
	 * sleeps for 1 second.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		startGateways();
		isStarted=true;
		log.info("Starting the Gateway Manager");
		while(isStarted){
			try{
				dispatchOutgoingMessages();
			}catch(Exception e){
				log.error("Error dispatching messages in the GatewayManager",e);
			}
			try{
				sleep(1000);
			}catch(InterruptedException e){
				log.error("Error while sleeping",e);
			}
		}
	}
	
	/**
	 * This method does the dispatching of outgoing messages.
	 * It pulls any messages from the database that have a status
	 * of 'outbox' and then dispatches them to the proper gateway.
	 * This method is separated out of its containing while loop
	 * for readability and easier testing. 
	 */
	private void dispatchOutgoingMessages(){
		//organize outgoing messages by protocol
		for(Protocol p: MessagingService.getProtocols()){
			//get all outgoing messages and the gateways that can send them
			List<Message> messages=null;
			//get the messages from the outbox
			Context.openSession();
			messages = messageService.getOutboxMessagesByProtocol(p);
			Context.closeSession();
			//get the supporting gateways
			List<MessagingGateway> gateways = getActiveSupportingGateways(p);
			//if there are no active supporting gateways, then we can't do anything
			if(gateways.size() < 1){
				return;
			}
			int messageIndex = rand.nextInt(gateways.size());
			int gatewayCount = gateways.size();
			for(Message message: messages){
				boolean messageSent =false;
				//give all the gateways the option to grab the message
				//this is used when the 'from' address specifically indicates
				//which gateway should be used for sending a message
				for(MessagingGateway gateway: gateways){
					if(gateway.shouldSendMessage(message)){
						try{
							gateway.sendMessage(message);
							messageSent=true;
						}catch(Exception e){
							log.error("Error sending message",e);
							messageSent=false;
						}
						break;
					}
				}
				//if no gateway 'wants' the message, we let another gateway have it
				if(!messageSent){
					//round robin
					try{
						gateways.get(messageIndex++ % gatewayCount).sendMessage(message);
						messageSent=true;
					}catch(Exception e){
						log.error("Error sending message",e);
						messageSent=false;
					}
				}
				//increment the number of send attempts
				message.setSendAttempts(message.getSendAttempts()+1);
				//update the message status
				if(messageSent){
					message.setMessageStatus(MessageStatus.SENT);
				}else{
					if(message.getSendAttempts() < maxRetry){
						message.setMessageStatus(MessageStatus.RETRYING);
					}else{
						message.setMessageStatus(MessageStatus.FAILED);
					}
				}
				//save the message
				Context.openSession();
				messageService.saveMessage(message);
				Context.closeSession();
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
	
	@SuppressWarnings("unchecked")
	public static <G extends MessagingGateway> G getGatewayByClass(Class<? extends G> gatewayClass){
		for(MessagingGateway g: gateways){
			if(g.getClass().equals(gatewayClass)){
				return (G) g;
			}
		}
		return null;
	}
	
}
