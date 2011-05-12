package org.openmrs.module.messaging.schedulertask;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.omail.OMailProtocol;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A scheduler task that polls the database every 2 seconds looking for messages
 * with a status of 'outbox' Once such messages are found, they are dispatched
 * to the proper gateway for sending.
 */
public class DispatchMessagesTask extends AbstractTask {

	private static Log log = LogFactory.getLog(DispatchMessagesTask.class);

	/**
	 * private cached instance of the module MessageService
	 */
	private MessageService messageService;

	/**
	 * The maximum number of times that this system will try to send a message
	 * before marking it as failed
	 */
	private Integer maxRetryAttempts;

	private final Random rand = new Random();

	/**
	 * This method does the dispatching of outgoing messages. It pulls any
	 * messages from the database that have a status of 'outbox' and then
	 * dispatches them to the proper gateway.
	 */
	public void execute() {
		long startTime = new Date().getTime();
		Context.openSession();
		try {
			// authenticate (for pre-1.7)
			if (!Context.isAuthenticated())
				authenticate();

			GatewayManager manager = getGatewayManager();
			if (manager == null){
				log.info("Gateway Manager null");
				return;
			}
			// receive messages
			log.info("Receiving messages");
			for (MessagingGateway mg : manager.getActiveGateways()) {
				if (mg.canReceive()) {
					log.info(mg.getName()+" receiving messages");
					mg.receiveMessages();
				}
			}

			// send messages
			log.debug("Dispatching messages");
			dispatchMessages(getMessageService().getOutboxMessages());

		} catch (Exception e) {
			log.error("Exception occurred during DispatchMessagesTask", e);
		} finally {
			Context.closeSession();
			long endTime= new Date().getTime();
			log.info("Time Elapsed: " + ((endTime - startTime)/1000.0) + " seconds.");
		}
	}
	
	private void dispatchMessages(List<Message> messages){
		List<MessagingGateway> gateways;
		for(Message message: messages){
			log.debug("Dispatching message: "+message.getContent());
			for(MessageRecipient mRecipient: message.getTo()){
				if(mRecipient.getMessageStatus() == MessageStatus.OUTBOX || mRecipient.getMessageStatus() == MessageStatus.RETRYING){
					log.debug("Dispatching message to "+mRecipient.getRecipient().getAddress());
					gateways = getGatewayManager().getActiveSupportingGateways(mRecipient.getRecipient().getProtocol());
					//TODO maybe we should change the message status if there aren't any available gateways
					if(gateways.size() <=0) continue;
					//increment the number of send attempts
					mRecipient.setSendAttempts(mRecipient.getSendAttempts()+1);
					try{
						//TODO code better gateway routing logic
						log.debug("Attempting to send message.");
						gateways.get(0).sendMessage(message,mRecipient);
						log.debug("Message sent");
						//set the last attempt date
						mRecipient.setDate(new Date());
						//set the status as sent
						mRecipient.setMessageStatus(MessageStatus.SENT);
					}catch(Exception e){
						log.error("Error sending message",e);
						//if the sending didn't work, update the message status
						if(mRecipient.getSendAttempts() < getMaxRetryAttempts()){
							mRecipient.setMessageStatus(MessageStatus.RETRYING);
							log.debug("Retrying message #" + message.getId());
						}else{
							mRecipient.setMessageStatus(MessageStatus.FAILED);
							log.debug("Message #" + message.getId()+ " failed");
						}
					}
					
				}
			}
			//set the sent/last attempt date
			message.setDate(new Date());
			//save the message
			log.debug("Saving Message");
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

	private GatewayManager getGatewayManager(){
		return Context.getService(MessagingService.class).getGatewayManager();
	}
	/**
	 * @return the maxRetryAttempts
	 */
	private Integer getMaxRetryAttempts() {
		if (maxRetryAttempts == null)
			try {
				maxRetryAttempts = Integer.parseInt(Context
						.getAdministrationService().getGlobalProperty(
								MessagingConstants.GP_MAX_RETRIES));
			} catch (Exception e) {
				maxRetryAttempts = 3;
			}
		return maxRetryAttempts;
	}
}
