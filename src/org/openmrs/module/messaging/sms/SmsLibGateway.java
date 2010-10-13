package org.openmrs.module.messaging.sms;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageStatus;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.Service.ServiceStatus;
import org.smslib.http.ClickatellHTTPGateway;

public class SmsLibGateway extends MessagingGateway implements IOutboundMessageNotification {

	private static Log log = LogFactory.getLog(SmsLibGateway.class);
	private Map<String, Message> sentMessages;

	public SmsLibGateway(){
		sentMessages = new HashMap<String, Message>();
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

	@Override
	public boolean shouldSendMessage(Message m) {
		return false;
	}

	@Override
	public void shutdown() {
		try {
			Service.getInstance().stopService();
		} catch (Exception e) {
			log.error("Error shutting down the SMSLib service.",e);
		}
	}

	@Override
	public void startup() {
		//detect the modems
		//AllModemsDetector.getService();
		if(Service.getInstance() != null && (Service.getInstance().getServiceStatus() == ServiceStatus.STARTED || Service.getInstance().getServiceStatus() == ServiceStatus.STARTING)){
			return;
		}
		ClickatellHTTPGateway gateway = new ClickatellHTTPGateway("clickatel", "3258971", "dieterich.lawson", "martyr441");
		gateway.setOutbound(true);
		gateway.setSecure(true);
		try {
			Service.getInstance().addGateway(gateway);
			log.info("Clickatell gateway added successfully");
		} catch (GatewayException e1) {
			log.error("Unable to add Clickatel gateway",e1);
		}
		
		try {
			Service.getInstance().startService();
		} catch (Exception  e2) {
			log.error("Unable to start service", e2);
		}
		log.info("SmsLib gateway has "+ Service.getInstance().getGateways().size() + " subgateways.");
		// set the message receiver to save the messages to the database
		Service.getInstance().setInboundMessageNotification(new IInboundMessageNotification() {
			
					private MessageService messageService = Context.getService(MessageService.class);
					private MessagingAddressService addressService = Context.getService(MessagingAddressService.class);

					public void process(AGateway gateway, MessageTypes messageType, InboundMessage message) {
						// TODO: Figure out how to get recipient number
						Context.openSession();
						Message m = new Message(gateway.getFrom(), message.getText());
						m.setSender(addressService.getPersonForAddress("+"+message.getOriginator()));
						m.setOrigin("+"+message.getOriginator());
						m.setMessageStatus(MessageStatus.RECEIVED);
						m.setDate(new Date());
						m.setProtocolId(SmsProtocol.PROTOCOL_ID);
						messageService.saveMessage(m);
						try {
							Service.getInstance().deleteMessage(message);
						} catch (Exception e) {
							log.error("Error deleting message from phone",e);
						}
						Context.closeSession();
					}
				});
	}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getProtocolId().equals(SmsProtocol.PROTOCOL_ID);
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
