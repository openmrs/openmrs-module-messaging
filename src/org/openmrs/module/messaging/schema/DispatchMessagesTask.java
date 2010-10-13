package org.openmrs.module.messaging.schema;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A scheduler task that polls the database every 2 seconds 
 * looking for messages with a status of 'outbox'
 * Once such messages are found, they are dispatched 
 * to the proper gateway for sending.
 */
public class DispatchMessagesTask extends AbstractTask{
	
	private static Log log = LogFactory.getLog(DispatchMessagesTask.class);
	
	private MessageService messageService;
	
	/** 
	 * The maximum number of times that this system will try 
	 * to send a message before marking it as failed 
	 */
	private int maxRetry;
		
	private final Random rand = new Random();

	public DispatchMessagesTask(){
		messageService = Context.getService(MessageService.class);
		//get the max number of retries
		try{
			maxRetry = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_MAX_RETRIES));
		}catch(Exception e){
			maxRetry = 3;
		}
	}

	/**
	 * This method does the dispatching of outgoing messages.
	 * It pulls any messages from the database that have a status
	 * of 'outbox' and then dispatches them to the proper gateway. 
	 */
	public void execute(){
		if(MessagingModuleActivator.manager == null) return;
		//organize outgoing messages by protocol
		log.debug("Dispatching messages");
		List<Protocol> protocols = Context.getService(MessagingService.class).getProtocols();
		for(Protocol p: protocols){
			//get all outgoing messages and the gateways that can send them
			List<Message> messages = messageService.getOutboxMessagesByProtocol(p);
			if(messages.size() <=0) continue;
			//get the supporting gateways
			List<MessagingGateway> gateways = MessagingModuleActivator.manager.getActiveSupportingGateways(p);
			//if there are no active supporting gateways, then we can't do anything
			if(gateways.size() < 1) continue;
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
							//set the sent date
							message.setDate(new Date());
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
				//set the sent/last attempt date
				message.setDate(new Date());
				//update the message status
				if(messageSent){
					message.setMessageStatus(MessageStatus.SENT);
				}else{
					if(message.getSendAttempts() < maxRetry){
						message.setMessageStatus(MessageStatus.RETRYING);
						log.info("Retrying message #" + message.getId());
					}else{
						message.setMessageStatus(MessageStatus.FAILED);
						log.info("Message #" + message.getId()+ " failed");
					}
				}
				//save the message
				messageService.saveMessage(message);
			}
		}
	}
}
