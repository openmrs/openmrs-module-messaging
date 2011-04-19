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
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
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
		Context.openSession();

		try {
			// authenticate (for pre-1.7)
			if (!Context.isAuthenticated())
				authenticate();

			GatewayManager manager = getGatewayManager();
			if (manager == null)
				return;

			// receive messages
			log.debug("Receiving messages");
			for (MessagingGateway mg : manager.getActiveGateways()) {
				if (mg.canReceive()) {
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
		}
	}

	private void dispatchMessages(List<Message> messages){
		List<MessagingGateway> gateways;
		for(Message message: messages){
			for(MessageRecipient mRecipient: message.getTo()){
				if(mRecipient.getMessageStatus() == MessageStatus.OUTBOX || mRecipient.getMessageStatus() == MessageStatus.RETRYING){
					gateways = getGatewayManager().getActiveSupportingGateways(mRecipient.getRecipient().getProtocol());
					//TODO maybe we should change the message status if there aren't any available gateways
					if(gateways.size() <=0) continue;
					//increment the number of send attempts
					mRecipient.setSendAttempts(mRecipient.getSendAttempts()+1);
					try{
						//TODO code better gateway routing logic
						gateways.get(0).sendMessage(message,mRecipient);
						//set the last attempt date
						mRecipient.setDate(new Date());
						//set the status as sent
						mRecipient.setMessageStatus(MessageStatus.SENT);
					}catch(Exception e){
						log.error("Error sending message",e);
						//if the sending didn't work, update the message status
						if(mRecipient.getSendAttempts() < getMaxRetryAttempts()){
							mRecipient.setMessageStatus(MessageStatus.RETRYING);
							log.info("Retrying message #" + message.getId());
						}else{
							mRecipient.setMessageStatus(MessageStatus.FAILED);
							log.info("Message #" + message.getId()+ " failed");
						}
					}
					
				}
			}
			//set the sent/last attempt date
			message.setDate(new Date());
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
