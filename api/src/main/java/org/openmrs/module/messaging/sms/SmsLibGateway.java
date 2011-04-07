package org.openmrs.module.messaging.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
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
	private Map<String, MessageRecipient> sentMessages;

	private SmsServiceManager serviceManager;

	public SmsLibGateway() {
		sentMessages = new HashMap<String, MessageRecipient>();
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
	public void sendMessage(Message message, MessageRecipient recipient) throws Exception {
		OutboundMessage om = new OutboundMessage(recipient.getRecipient().getAddress(), message.getContent());
		om.setId(UUID.randomUUID().toString());
		sentMessages.put(om.getId(), recipient);
		Service.getInstance().queueMessage(om);	
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
		MessageRecipient recipient = sentMessages.get(oMessage.getId());
		// set the new status
		if (oMessage.getMessageStatus() == MessageStatuses.SENT) {
			//set the status of the changed outbound messge to sent
			recipient.setMessageStatus(MessageStatus.SENT);
			//remove the status list and its associated message from the map
			sentMessages.remove(oMessage.getId());
		} else if (oMessage.getMessageStatus() == MessageStatuses.FAILED) { // if the message failed
			log.error("Message failed: " + oMessage.getErrorMessage());
			//set the OpenMRS message status
			recipient.setMessageStatus(MessageStatus.FAILED);
			//remove the status list and its associated message from the map
			sentMessages.remove(oMessage.getId());
		}
		//save the message
		getMessageService().saveMessage(recipient.getMessage());
	}
}
