package org.openmrs.module.messaging.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Person;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;

/**
 * A text based message sent within the OpenMRS messaging
 * framework. In general, you should not call
 * the message constructor directly but should instead create Messages via a class
 * that implements the {@link Protocol} interface. Messages created
 * outside of message factories are not guaranteed to have properly formatted.
 * 
 * @see Protocol
 */
public class Message extends BaseOpenmrsObject {

	protected Message() {super();}
	
	private static final Log log = LogFactory.getLog(Message.class);

	protected Integer messageId;

	// content
	/**
	 * The string content of the message.
	 */
	protected String content;

	/**
	 * The subject of this message. Be aware that not all {@link MessagingGateway}s will be able
	 * to include a subject. The max length is 256 characters (UTF8).
	 */
	protected String subject;
	
	//header info
	
	/**
	 * The person that sent this message
	 */
	protected Person sender;
	
	/**
	 * A list of MessagingAddresses representing the recipients of this message.
	 * MessagingAddresses do not necessarily have a non-null person field.
	 */
	protected Set<MessageRecipient> to;
	
	/**
	 * The date that the message was sent or received. Intentionally left a little ambiguous.
	 */
	protected Date date = new Date();

	/**
	 * The message that this message is replying to.
	 */
	private Message inReplyTo;

	public Message(String content, String to, Class<? extends Protocol> protocolClass){
		super();
		this.to = new HashSet<MessageRecipient>();
		this.content = content;
		getTo().add(new MessageRecipient(new MessagingAddress(to,null,protocolClass),this));
	}
	
	/**
	 * Creates a message with only a destination and content. The date and
	 * origin are filled in automatically at the time of sending.
	 * 
	 * @param content
	 *            the content of the message
	 */
	public Message(MessagingAddress to, String content) {
		super();
		this.to = new HashSet<MessageRecipient>();
		this.content = content;
		getTo().add(new MessageRecipient(to,this));
	}

	/**
	 * Creates a messaging address with 1 destination and an origin.
	 * @param to
	 * @param from
	 * @param content
	 */
	public Message(MessagingAddress to, Person from, String content) {
		super();
		this.to = new HashSet<MessageRecipient>();
		this.content = content;
		this.setSender(from);
		getTo().add(new MessageRecipient(to,this));
	}
	
	/**
	 * Creates a messaging address with several destinations and an origin.
	 * @param to
	 * @param from
	 * @param content
	 */
	public Message(Set<MessagingAddress> to, Person from, String content) {
		super();
		this.content = content;
		this.setRecipients(to);
		this.setSender(from);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		for(MessageRecipient recipient:to){
			recipient.setDate(date);
		}
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	
	public Integer getId() {
		return messageId;
	}

	public void setId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the recipients
	 */
	public Set<MessageRecipient> getTo() {
		return to;
	}
	
	public void setTo(Set<MessageRecipient> to){
		this.to = to;
	}
	
	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(Set<MessagingAddress> to) {
		this.to = new HashSet<MessageRecipient>();
		for(MessagingAddress addr:to){
			this.to.add(new MessageRecipient(addr,this));
		}
	}
	
	public boolean addDestination(MessagingAddress destination){
		if(destination == null){
			return false;
		}else{
			return to.add(new MessageRecipient(destination,this));
		}	
	}
	
	public Set<String> getToAddresses(){
		Set<String> addresses = new HashSet<String>();
		for(MessageRecipient recipient: to){
			if(recipient.getRecipient().getAddress() != null){
				addresses.add(recipient.getRecipient().getAddress());
			}
		}
		return addresses;
	}
	
	public Set<Person> getToPeople(){
		Set<Person> people = new HashSet<Person>();
		for(MessageRecipient recipient: to){
			if(recipient.getRecipient().getPerson() != null){
				people.add(recipient.getRecipient().getPerson());
			}
		}
		return people;
	}
	
	public String getDisplayOrigin(){
		if(getSender() != null){
			return getSender().getPersonName().toString();
		}else{
			return "";
		}
	}
	
	public String getDisplayDestination(){
		if(to != null){
			StringBuilder destination = new StringBuilder();
			for(MessageRecipient recipient: to){
				if(recipient.getRecipient().getPerson() != null){
					destination.append(recipient.getRecipient().getPerson().getPersonName().toString()+ " ");
				}
				destination.append("<"+recipient.getRecipient().getAddress()+">");
			}
			return destination.toString();
		}
		return "";
	}
	
	/**
	 * @param sender the sender to set
	 */
	public void setSender(Person sender) {
		this.sender = sender;
	}

	/**
	 * @return the sender
	 */
	public Person getSender() {
		return sender;
	}
	
	public void setOrigin(String origin){
		for(MessageRecipient recipient:to){
			recipient.setOrigin(origin);
		}
	}
	/**
	 * @param inReplyTo the inReplyTo to set
	 */
	public void setInReplyTo(Message inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	/**
	 * @return the inReplyTo
	 */
	public Message getInReplyTo() {
		return inReplyTo;
	}

	
	public void setStatus(MessageStatus status){
		for(MessageRecipient recipient:to){
			recipient.setMessageStatus(status);
		}
	}
	
	public enum MessageFields{
		DATE("date"),
		IN_REPLY_TO("inReplyTo"),
		TO("to"),
		SENDER("sender"),
		SUBJECT("subject"),
		CONTENT("content"),
		MESSAGE_ID("messageId");
		public final String name;
		private MessageFields(String name){ this.name = name; }
		public String toString(){ return name; }
	}
}
