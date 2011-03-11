package org.openmrs.module.messaging.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.Modem;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.sms.servicemanager.SmsServiceManager;
import org.openmrs.module.messaging.sms.servicemanager.exception.ServiceStateException;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.Service.ServiceStatus;

public class SmsLibGateway extends MessagingGateway implements IOutboundMessageNotification {

	private static Log log = LogFactory.getLog(SmsLibGateway.class);
	private Map<List<OutboundMessageStatus>, Message> sentMessages;

	private SmsServiceManager serviceManager;

	public SmsLibGateway() {
		sentMessages = new HashMap<List<OutboundMessageStatus>, Message>();
		serviceManager = new SmsServiceManager();
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public boolean canSend() {
		return true;
	}

	@Override
	public String getDescription() {
		return "A gateway for sending SMS via a variety of methods powered by SMSLib";
	}

	@Override
	public String getName() {
		return "SMSLib";
	}

	@Override
	public boolean isActive() {
		return Service.getInstance() != null
				&& Service.getInstance().getServiceStatus() == ServiceStatus.STARTED;
	}

	@Override
	public void sendMessage(Message message) throws Exception {
		// create a list to contain references to all the outbound
		// messages we're about to create
		List<OutboundMessageStatus> outboundStatuses = new ArrayList<OutboundMessageStatus>();
		for (MessagingAddress toAddress : message.getTo()) {
			OutboundMessage om = new OutboundMessage(toAddress.getAddress(), message.getContent());
			om.setId(UUID.randomUUID().toString());
			outboundStatuses.add(new OutboundMessageStatus(om));
			Service.getInstance().queueMessage(om);
		}
		sentMessages.put(outboundStatuses, message);
	}

	public void receiveMessages() {
		List<InboundMessage> inboundMessages = new ArrayList<InboundMessage>();
		try {
			Service.getInstance().readMessages(inboundMessages, MessageClasses.ALL);
		} catch (Throwable e) {
			log.error("Error reading messages from phone", e);
		}
		for (InboundMessage iMessage : inboundMessages) {
			//create the message
			Message m = new Message(Service.getInstance().getGateway(iMessage.getGatewayId()).getFrom(), iMessage.getText());
			m.setSender(getAddressService().getPersonForAddress("+" + iMessage.getOriginator()));
			m.setOrigin("+" + iMessage.getOriginator());
			m.setStatus(MessageStatus.RECEIVED);
			m.setDate(iMessage.getDate());
			m.setProtocol(SmsProtocol.class);
			getMessageService().saveMessage(m);
			//try to delete the message from the phone
			try {
				Service.getInstance().deleteMessage(iMessage);
			} catch (Exception e) {
				log.error("Error deleting message from phone", e);
			}
		}
	}

	@Override
	public void shutdown() {
		try {
			serviceManager.teardownService();
		} catch (Exception e) {
			log.error("Error shutting down the SMSLib service.", e);
		}
	}

	@Override
	public void startup() {
		try {
			serviceManager.initializeService();
			log.info("SmsLib gateway has been started with " + Service.getInstance().getGateways().size() + " subgateways.");
		} catch (ServiceStateException e) {
			log.error("Error initializing service", e);
		}
	}

	public List<Modem> redetectModems() throws ServiceStateException {
		serviceManager.teardownService();
		serviceManager.initializeService();
		return serviceManager.getDetectedModemBeans();
	}

	public List<Modem> getCurrentlyConnectedModems(boolean update) {
		if (update) {
			return serviceManager.updateModemBeans();
		}
		return serviceManager.getDetectedModemBeans();
	}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getClass().equals(SmsProtocol.class);
	}

	/**
	 * Called when something happens with an outbound message that was sent -
	 * should not be called in your code. This method retrieves the OpenMRS 
	 * message associated with the OutboundMessage and updates its status accordingly.
	 * 
	 * @see org.smslib.IOutboundMessageNotification#process(java.lang.String,
	 *      org.smslib.OutboundMessage)
	 */
	public void process(AGateway gateway, OutboundMessage oMessage) {
		// retrieve the OpenMRS message associated with this OutboundMessage
		Message mesg = null;
		List<OutboundMessageStatus> outboundStatuses = null;
		OutboundMessageStatus outboundStatus = null;
		outerloop:
		for (List<OutboundMessageStatus> statuses : sentMessages.keySet()) {
			for(OutboundMessageStatus status:statuses){
				if(status.getUuid().equals(oMessage.getId())){
					mesg = sentMessages.get(statuses);
					outboundStatuses = statuses;
					outboundStatus = status;
					break outerloop;
				}
			}
		}
		//if any parts have not been found, return
		if(mesg == null || outboundStatuses == null || outboundStatus == null){
			return;
		}
		// set the new status
		if (oMessage.getMessageStatus() == MessageStatuses.SENT) {
			//remove the status list and its associated message from the map
			sentMessages.remove(outboundStatuses);
			//set the status of the changed outbound messge to sent
			outboundStatus.setStatus(MessageStatus.SENT);
			//check if all the outbound messages have been sent (we're done if they are)
			boolean allSent = true;
			for(OutboundMessageStatus stat: outboundStatuses){
				if(stat.getStatus() != MessageStatus.SENT) allSent = false;
			}
			//if all messages have been sent, then we're done - 
			//we don't have to re-add the message & statuses to the map,
			//and we can just modify the OpenMRS message status and be done
			if(allSent){
				mesg.setStatus(MessageStatus.SENT);				
			}else{ //otherwise, we have to re-add the message & statuses
				sentMessages.put(outboundStatuses, mesg);
			}
		} else if (oMessage.getMessageStatus() == MessageStatuses.FAILED) { // if the message failed
			log.error("Message failed: " + oMessage.getErrorMessage());
			//set the OpenMRS message status
			mesg.setStatus(MessageStatus.FAILED);
			//remove the status list and its associated message from the map
			sentMessages.remove(outboundStatuses);
		}
		//save the message
		getMessageService().saveMessage(mesg);
	}

	private class OutboundMessageStatus {
		private String uuid;
		private MessageStatus status;

		public OutboundMessageStatus(OutboundMessage message) {
			this.uuid = message.getId();
			this.status = MessageStatus.OUTBOX;
		}
		
		public String getUuid() { return uuid; }
		public void setStatus(MessageStatus status) { this.status = status; }
		public MessageStatus getStatus() { return status; }
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof OutboundMessageStatus) {
				OutboundMessageStatus castOther = (OutboundMessageStatus) other;
				if (castOther.getUuid().equals(this.getUuid())
						&& castOther.getStatus().equals(this.getStatus())) {
					return true;
				}
			}
			return false;
		}
	}
}
