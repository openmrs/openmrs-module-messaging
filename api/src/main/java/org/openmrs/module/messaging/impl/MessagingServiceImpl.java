package org.openmrs.module.messaging.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.domain.gateway.exception.AddressFormattingException;
import org.openmrs.module.messaging.domain.gateway.exception.MessageFormattingException;
import org.openmrs.module.messaging.domain.listener.IncomingMessageListener;
import org.openmrs.module.messaging.email.EmailProtocol;
import org.openmrs.module.messaging.omail.OMailProtocol;
import org.openmrs.module.messaging.sms.SmsProtocol;

/**
 * The implementation of the MessagingService interface 
 */
public class MessagingServiceImpl extends BaseOpenmrsService implements MessagingService {
	
	private static Log log = LogFactory.getLog(MessagingServiceImpl.class);
			
	/**
	 * A map containing all the messaging protocols that this system can use
	 */
	private Map<Class<? extends Protocol>, Protocol> protocols;
		
	/**
	 * A thread-safe ArrayList of all the message listeners
	 */
	private CopyOnWriteArrayList<IncomingMessageListener> listeners;
	
	/**
	 * The object that handles all gateway management tasks
	 */
	private GatewayManager gatewayManager;
	
	public MessagingServiceImpl(){
		//setup the listeners
		listeners = new CopyOnWriteArrayList<IncomingMessageListener>();
		
		listeners.add(new IncomingMessageListener() {	
			public void messageRecieved(Message message) {
				log.info("INCOMING MESSAGE RECIEVED: "+ message.getContent() + ". SENDER: "+ message.getSender().getPersonName());
				for(MessageRecipient mr: message.getTo()){
					if(mr.getProtocol().equals(OMailProtocol.class) && mr.getRecipient().getPerson() != null){
						PersonAttribute shouldAlertAttr = mr.getRecipient().getPerson().getAttribute(MessagingModuleActivator.SEND_OMAIL_ALERTS_ATTR_NAME);
						if(shouldAlertAttr != null && Boolean.parseBoolean(shouldAlertAttr.getValue())){
							int addressId = Integer.parseInt(mr.getRecipient().getPerson().getAttribute(MessagingModuleActivator.ALERT_ADDRESS_ATTR_NAME).getValue());
							MessagingAddress alertAddr = Context.getService(MessagingAddressService.class).getMessagingAddress(addressId);
							Message alertMsg = new Message(alertAddr,"You have new Omail");
							try {
								sendMessage(alertMsg);
							} catch (Exception e) {
								log.error("Unable to send alert message",e);
							}
						}
					}
				}
			}
		});

		//initialize the protocols
		protocols = new HashMap<Class<? extends Protocol>, Protocol>();
		protocols.put(SmsProtocol.class, new SmsProtocol());
		protocols.put(OMailProtocol.class, new OMailProtocol());
		protocols.put(EmailProtocol.class, new EmailProtocol());
	}
	
	public void sendMessage(String message, String address, Class<? extends Protocol> protocolClass) throws Exception{
		if(protocolClass == null) throw new Exception("Invalid protocol class");
		if(address == null || address.equals("")) throw new Exception("Cannot send to empty address");
		if(message == null) throw new Exception("Cannot send null message");
		sendMessage(new Message(message, address, protocolClass));
	}
	
	public void sendMessage(Message message) throws Exception{
		//check if there are destinations
		if(message.getTo() == null || message.getTo().size() == 0){
			throw new Exception("No destination specified.");
		}
		for(MessageRecipient mRecipient:message.getTo()){
			Protocol p = getProtocolByClass(mRecipient.getProtocol());
			//check the protocol
			if(p == null) throw new Exception("Invalid protocol");
			//check the address
			if(!p.addressIsValid(mRecipient.getRecipient().getAddress())){
				throw new AddressFormattingException("Badly formatted address: "+mRecipient.getRecipient().getAddress());
			}
			//check the message
			if(!p.messageContentIsValid(message.getContent())){
				throw new MessageFormattingException("Cannot send this message to " + p.getProtocolName() + " addresses.");
			}
			if(!canSendToProtocol(p.getClass())){
				throw new Exception("There is not currently a gateway running" +
						" that can send " + p.getProtocolName() + " messages.");
			}
			mRecipient.setMessageStatus(MessageStatus.OUTBOX);
		}
		Context.getService(MessageService.class).saveMessage(message);
	}
	
	public void sendMessages(Set<Message> messages) throws Exception{
		for(Message m: messages){
			sendMessage(m);
		}
	}

	public List<Protocol> getProtocols(){
		return new ArrayList<Protocol>(protocols.values());
	}
	
	public <P extends Protocol> P getProtocolByClass(Class<P> clazz) {
		return (P) protocols.get(clazz);
	}
	
	public Protocol getProtocolByAbbreviation(String abbrev) {
		for(Protocol p: protocols.values()){
			if(p.getProtocolAbbreviation().equals(abbrev)) return p;
		}
		return null;
	}
	
	public boolean canSendToProtocol(Class<? extends Protocol> p){
		if(gatewayManager == null) return false;
		return gatewayManager.getActiveSupportingGateways(p).size() > 0;
	}
	

	public void registerListener(IncomingMessageListener listener){
		listeners.addIfAbsent(listener);
	}
	
	public void unregisterListener(IncomingMessageListener listener){
		listeners.remove(listener);
	}
	
	public void notifyListeners(Message message){
		for(IncomingMessageListener listener: listeners){
			listener.messageRecieved(message);
		}
	}

	public GatewayManager getGatewayManager() {
		return gatewayManager;
	}	
	
	public void setGatewayManager(GatewayManager manager){
		this.gatewayManager = manager;
	}
}
