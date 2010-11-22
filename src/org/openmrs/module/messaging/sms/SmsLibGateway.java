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
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.sms.servicemanager.SmsServiceManager;
import org.openmrs.module.messaging.sms.servicemanager.exception.ServiceStateException;
import org.openmrs.module.messaging.web.domain.ModemBean;
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
	private Map<String, Message> sentMessages;
	
	private SmsServiceManager serviceManager;
	
	public SmsLibGateway(){
		sentMessages = new HashMap<String, Message>();
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
		return Service.getInstance() != null && Service.getInstance().getServiceStatus() == ServiceStatus.STARTED;
	}

	@Override
	public void sendMessage(Message message) throws Exception{
		OutboundMessage om = new OutboundMessage(message.getDestination(), message.getContent());
		// we need to associate the SMSLib 'OutboundMessage' and the OpenMRS 'Message'
		// so that later when we get info about the status of the outbound message
		// we know which Message to modify
		om.setId(UUID.randomUUID().toString());
		sentMessages.put(om.getId(), message);
		Service.getInstance().queueMessage(om);
	}
	
	public void recieveMessages(){
		List<InboundMessage> inboundMessages = new ArrayList<InboundMessage>();
		try {
			Service.getInstance().readMessages(inboundMessages,MessageClasses.READ);
		} catch (Throwable e) {
			log.error("Error reading messages from phone", e);
		}
		for(InboundMessage iMessage: inboundMessages){
			Message m = new Message(Service.getInstance().getGateway(iMessage.getGatewayId()).getFrom(), iMessage.getText());
			m.setSender(addressService.getPersonForAddress("+"+iMessage.getOriginator()));
			m.setOrigin("+"+iMessage.getOriginator());
			m.setMessageStatus(MessageStatus.RECEIVED);
			m.setDate(iMessage.getDate());
			m.setProtocolId(SmsProtocol.class.getName());
			messageService.saveMessage(m);
			try {
				Service.getInstance().deleteMessage(iMessage);
			} catch (Exception e) {
				log.error("Error deleting message from phone",e);
			}
		}	
	}

	@Override
	public void shutdown() {
		try {
			serviceManager.teardownService();
		} catch (Exception e) {
			log.error("Error shutting down the SMSLib service.",e);
		}
	}

	@Override
	public void startup() {
		try{
			serviceManager.initializeService();
			log.info("SmsLib gateway has been started with "+ Service.getInstance().getGateways().size() + " subgateways.");
		}catch(ServiceStateException e){
			log.error("Error initializing service",e);
		}
	}
	
	public List<ModemBean> redetectModems() throws ServiceStateException{
		serviceManager.teardownService();
		serviceManager.initializeService();
		return serviceManager.getDetectedModemBeans();
	}
	
	public List<ModemBean> getCurrentlyConnectedModems(boolean update){
		if(update){
			return serviceManager.updateModemBeans();
		}
		return serviceManager.getDetectedModemBeans();
	}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getProtocolId().equals(SmsProtocol.class.getName());
	}

	/**
	 * Called when something happens with an outbound message that was sent.
	 * This method retrieves the openmrs Message associated with the
	 * OutboundMessage and updates its status accordingly
	 * 
	 * @see org.smslib.IOutboundMessageNotification#process(java.lang.String,
	 *      org.smslib.OutboundMessage)
	 */
	public void process(AGateway gateway, OutboundMessage oMessage) {
		// retrieve the OpenMRS message associated with this OutboundMessage
		Message m = sentMessages.get(oMessage.getId());
		// set the new status
		if (oMessage.getMessageStatus() == MessageStatuses.SENT) {
			m.setMessageStatus(MessageStatus.SENT);
			sentMessages.remove(oMessage.getId());
		} else if (oMessage.getMessageStatus() == MessageStatuses.FAILED) {
			log.error("Message failed: "+ oMessage.getErrorMessage());
			m.setMessageStatus(MessageStatus.FAILED);
			sentMessages.remove(oMessage.getId());
		} else if (oMessage.getMessageStatus() == MessageStatuses.UNSENT) {
			log.error("Message unsent, retrying: "+ oMessage.getErrorMessage());
			m.setMessageStatus(MessageStatus.RETRYING);
		}
		messageService.saveMessage(m);
	}

}
