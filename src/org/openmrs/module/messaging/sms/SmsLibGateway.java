package org.openmrs.module.messaging.sms;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageStatus;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.sms.util.AllModemsDetector;
import org.smslib.AGateway;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.Service.ServiceStatus;

public class SmsLibGateway extends MessagingGateway implements
		IOutboundMessageNotification {

	private Service service;

	private Map<String, Message> sentMessages;

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public boolean canSend() {
		return true;
	}

	@Override
	public boolean canSendFromUserAddresses(Protocol protocol) {
		return false;
	}

	@Override
	public MessagingAddress getDefaultSenderAddress() {
		return null;
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
		return service.getServiceStatus() == ServiceStatus.STARTED;
	}

	@Override
	public void sendMessage(Message message) {
		OutboundMessage om = new OutboundMessage(message.getDestination(), message.getContent());
		// we need to associate the SMSLib 'OutboundMessage' and the OpenMRS
		// 'Message'
		// so that later when we get info about the status of the outbound
		// message
		// we know which Message to modify
		om.setId(UUID.randomUUID().toString());
		sentMessages.put(om.getId(), message);
		service.queueMessage(om);
	}

	@Override
	public boolean shouldSendMessage(Message m) {
		return false;
	}

	@Override
	public void shutdown() {
		try {
			service.stopService();
		} catch (Exception e) {

		}
	}

	@Override
	public void startup() {
		service = AllModemsDetector.getService();
		// set the message receiver to save the messages to the database
		service
				.setInboundMessageNotification(new IInboundMessageNotification() {
					private MessageService messageService = Context
							.getService(MessageService.class);
					private MessagingAddressService addressService = Context
							.getService(MessagingAddressService.class);

					public void process(AGateway gateway,
							MessageTypes messageType, InboundMessage message) {
						// TODO: Figure out how to get recipient number
						Message m = new Message("", message.getOriginator());
						m.setSender(addressService.getPersonForAddress(m
								.getOrigin()));
						m.setRecipient(addressService.getPersonForAddress(m
								.getDestination()));
						m.setStatus(MessageStatus.RECEIVED);
						m.setDate(new Date());
						messageService.saveMessage(m);
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
			m.setStatus(MessageStatus.SENT);
			sentMessages.remove(oMessage.getId());
		} else if (oMessage.getMessageStatus() == MessageStatuses.FAILED) {
			m.setStatus(MessageStatus.FAILED);
			sentMessages.remove(oMessage.getId());
		} else if (oMessage.getMessageStatus() == MessageStatuses.UNSENT) {
			m.setStatus(MessageStatus.RETRYING);
		}
		messageService.saveMessage(m);
	}

}
