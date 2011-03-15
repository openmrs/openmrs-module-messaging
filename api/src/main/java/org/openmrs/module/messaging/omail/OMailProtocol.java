package org.openmrs.module.messaging.omail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.domain.gateway.exception.AddressFormattingException;
import org.openmrs.module.messaging.domain.gateway.exception.MessageFormattingException;

public class OMailProtocol extends Protocol {

	/**
	 * You should NEVER create Omail addresses and
	 * save them to the database. One OMail address
	 * will be saved in the database per person automatically
	 * 
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#createAddress(java.lang.String, org.openmrs.Person)
	 */
	@Override
	public MessagingAddress createAddress(String address, Person person) throws AddressFormattingException {
		if(person != null){
			MessagingAddress mAddress = new MessagingAddress(person.getPersonId().toString(),person);
			mAddress.setProtocol(this.getClass());
			return mAddress;
		}else if(address != null && !address.equalsIgnoreCase("")){
			Person p = Context.getPersonService().getPerson(Integer.valueOf(address));
			if(p != null){
				MessagingAddress mAddress = new MessagingAddress(address,p);
				return mAddress;
			}
		}
			return null;
	}

	/**
	 * This method is not implemented because OMail does not use
	 * string addresses. Use createMesage(String content, Person recipient) instead.
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#createMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Message createMessage(String messageContent, String to, String from) throws MessageFormattingException, AddressFormattingException {
		return null;
	}
	
	@Override
	public Message createMessage(String messageContent, MessagingAddress to, MessagingAddress from) throws MessageFormattingException {
		if(to.getPerson()!= null){
			Message m = new Message(to, from, messageContent);
			m.setProtocol(this.getClass());
			return m;
		}else{
			return null;
		}
	}

	@Override
	public Message createMessage(String messageContent, Set<MessagingAddress> to, MessagingAddress from) throws MessageFormattingException {
		for(MessagingAddress toAddress:to){
			if(toAddress.getPerson() == null){
				return null;
			}
		}
		Message m = new Message(to, from, messageContent);
		m.setProtocol(this.getClass());
		return m;
	}
	
	public Message createMessage(String messageContent, Person recipient, Person sender){
		MessagingAddress to=null, from=null;
		if(recipient != null){
			to = new MessagingAddress(recipient.getPersonId().toString(),recipient);
			to.setProtocol(this.getClass());
		}else{
			return null;
		}
		if(sender != null){
			from = new MessagingAddress(sender.getPersonId().toString(),sender);
			from.setProtocol(this.getClass());
		}
		Message m = new Message(to, from, messageContent);
		m.setProtocol(this.getClass());
		return m;	
	}
	
	public Message createMessage(String messageContent, Collection<Person> recipients, Person sender){
		MessagingAddress from=null;
		Set<MessagingAddress> to = new HashSet<MessagingAddress>();
		if(recipients != null){
			for(Person p: recipients){
				MessagingAddress ma = new MessagingAddress(p.getPersonId().toString(),p);
				ma.setProtocol(this.getClass());
				to.add(ma);
			}
		}else{
			return null;
		}
		if(sender != null){
			from = new MessagingAddress(sender.getPersonId().toString(),sender);
			from.setProtocol(this.getClass());
		}
		Message m = new Message(to, from, messageContent);
		m.setProtocol(this.getClass());
		return m;	
	}

	@Override
	public String getProtocolAbbreviation() {
		return "omail";
	}

	@Override
	public String getProtocolName() {
		return "OMail";
	}

	/**
	 * With OMail, the message content is always valid
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean messageContentIsValid(String content) {
		return true;
	}
	
	/**
	 * Because OMail addreses depend on the person, not the text of the address, the address is always valid
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean addressIsValid(String address) {
		return true;
	}
}