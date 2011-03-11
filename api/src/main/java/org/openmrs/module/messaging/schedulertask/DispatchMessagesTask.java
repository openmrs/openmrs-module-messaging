package org.openmrs.module.messaging.schedulertask;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A scheduler task that polls the database every 2 seconds 
 * looking for messages with a status of 'outbox'
 * Once such messages are found, they are dispatched 
 * to the proper gateway for sending.
 */
public class DispatchMessagesTask extends AbstractTask{
	
	private static Log log = LogFactory.getLog(DispatchMessagesTask.class);
	
	/**
	 * private cached instance of the module MessageService
	 */
	private MessageService messageService;
	
	/** 
	 * The maximum number of times that this system will try 
	 * to send a message before marking it as failed 
	 */
	private Integer maxRetryAttempts;
		
	private final Random rand = new Random();

	/**
	 * This method does the dispatching of outgoing messages.
	 * It pulls any messages from the database that have a status
	 * of 'outbox' and then dispatches them to the proper gateway. 
	 */
	public void execute(){
		Context.openSession();

		try {
			// authenticate (for pre-1.7)
			if (!Context.isAuthenticated())
				authenticate();
			
			GatewayManager manager = Context.getService(MessagingService.class).getGatewayManager(); 
			if(manager == null) return;
			
			//organize outgoing messages by protocol
			log.debug("Dispatching messages");
			List<Protocol> protocols = Context.getService(MessagingService.class).getProtocols();
			for(Protocol p: protocols){
				
				//start by receiving the messages
				for(MessagingGateway gateway: manager.getSupportingGateways(p)){
					if(gateway.canReceive())
						gateway.receiveMessages();
				}
				
				//get all outgoing messages and the gateways that can send them
				List<Message> messages = getMessageService().getOutboxMessagesByProtocol(p.getClass());
				if(messages.size() <=0) continue;
				
				//get the supporting gateways
				List<MessagingGateway> gateways = manager.getActiveSupportingGateways(p);
				
				//if there are no active supporting gateways, then we can't do anything
				if(gateways.size() < 1) continue;
				
				//dispatch the messages
				dispatchMessages(gateways,messages);
			}
		} catch (Exception e) {
			log.error("Exception occurred during DispatchMessagesTask", e);
		} finally {
			Context.closeSession();
		}
	}
	
	private void dispatchMessages(List<MessagingGateway> gateways, List<Message> messages){
		int messageIndex = rand.nextInt(gateways.size());
		int gatewayCount = gateways.size();
		for(Message message: messages){
			
			//increment the number of send attempts
			message.setSendAttempts(message.getSendAttempts()+1);
			
			//set the sent/last attempt date
			message.setDate(new Date());
			
			try{
				//round robin
				gateways.get(messageIndex++ % gatewayCount).sendMessage(message);
				//set the status as sent
				message.setStatus(MessageStatus.SENT);
			}catch(Exception e){
				log.error("Error sending message",e);
				//if the sending didn't work, update the message status
				if(message.getSendAttempts() < getMaxRetryAttempts()){
					message.setStatus(MessageStatus.RETRYING);
					log.info("Retrying message #" + message.getId());
				}else{
					message.setStatus(MessageStatus.FAILED);
					log.info("Message #" + message.getId()+ " failed");
				}
			}
			
			//save the message
			getMessageService().saveMessage(message);
		}
	}

	/**
	 * @return the messageService
	 */
	private MessageService getMessageService() {
		if (messageService == null)
			messageService = Context.getService(MessageService.class);		
		return messageService;
	}

	/**
	 * @return the maxRetryAttempts
	 */
	private Integer getMaxRetryAttempts() {
		if (maxRetryAttempts == null)
			try{
				maxRetryAttempts = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_MAX_RETRIES));
			}catch(Exception e){
				maxRetryAttempts = 3;
			}
		return maxRetryAttempts;
	}
	
	
}
