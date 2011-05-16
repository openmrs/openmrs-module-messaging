package org.openmrs.module.messaging.omail;

import java.util.Date;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public class OMailGateway extends MessagingGateway {

	private boolean started = false;

	public OMailGateway(){}
	
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
		return "A gateway for sending intra-OpenMRS mail";
	}

	@Override
	public String getName() {
		return "OMail Gateway";
	}

	@Override
	public boolean isActive() {
		return started;
	}

	@Override
	public void receiveMessages() {
		List<Message> incomingMsgs = getMessageService().getMessagesForProtocolAndStatus(OMailProtocol.class, MessageStatus.SENT.getNumber());
		for(Message m: incomingMsgs){
			for(MessageRecipient recipient: m.getTo()){
				if(recipient.getProtocol().equals(OMailProtocol.class)){
					recipient.setMessageStatus(MessageStatus.RECEIVED);
					recipient.setDate(new Date());
				}
			}
			getMessageService().saveMessage(m);
		}
	}

	@Override
	public void sendMessage(Message message, MessageRecipient recipient) throws Exception {
		recipient.setOrigin(message.getSender().getId().toString());
	}

	@Override
	public void shutdown() {/*do nothing*/}

	@Override
	public void startup() {
		try{
			initializeAddresses();
			started =true;
		}catch(Throwable t){
			started=false;
		}
	}
	
	private void initializeAddresses(){
			List<Person> people = Context.getPersonService().getPeople("", false);
			for(Person p: people){
				List<MessagingAddress> addresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPerson(p,OMailProtocol.class,false);
				if(addresses.size() ==0){
					MessagingAddress ma = new MessagingAddress(p.getPersonId().toString(),p,OMailProtocol.class);
					Context.getService(MessagingAddressService.class).saveMessagingAddress(ma);
				}
			}
	}

	@Override
	public boolean supportsProtocol(Class<? extends Protocol> p) {
		return p.equals(OMailProtocol.class);
	}
}