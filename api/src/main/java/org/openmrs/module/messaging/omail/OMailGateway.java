package org.openmrs.module.messaging.omail;

import java.util.Date;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public class OMailGateway extends MessagingGateway {

	public OMailGateway(){
		List<Person> people = Context.getPersonService().getPeople("", false);
		for(Person p: people){
			List<MessagingAddress> addresses = getAddressService().getMessagingAddressesForPerson(p);
			boolean hasOMailAddress=false;
			for(MessagingAddress add:addresses){
				if(add.getProtocol() == OMailProtocol.class) hasOMailAddress=true;
			}
			if(!hasOMailAddress){
				MessagingAddress ma = new MessagingAddress(p.getPersonId().toString(),p);
				ma.setProtocol(OMailProtocol.class);
				getAddressService().saveMessagingAddress(ma);
			}
		}
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
		return "A gateway for sending intra-OpenMRS mail";
	}

	@Override
	public String getName() {
		return "OMail Gateway";
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void receiveMessages() {
		List<Message> incomingMsgs = getMessageService().getOutboxMessagesByProtocol(OMailProtocol.class);
		for(Message m: incomingMsgs){
			m.setMessageStatus(MessageStatus.RECEIVED);
			getMessageService().saveMessage(m);
		}
	}

	@Override
	public void sendMessage(Message message) throws Exception {
		message.setMessageStatus(MessageStatus.OUTBOX);
		message.setDate(new Date());
		getMessageService().saveMessage(message);
	}

	@Override
	public void shutdown() {/*do nothing*/}

	@Override
	public void startup() {/*do nothing*/}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getClass() == OMailProtocol.class;
	}
}