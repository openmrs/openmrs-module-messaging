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
 * the message constructor directly but should instead create them via a class
 * that implements the {@link Protocol} interface. Messages created
 * outside of message factories are not guaranteed to have properly formatted.
 * 
 * @see Protocol
 */
public class Message extends BaseOpenmrsObject {

	protected Message() {}
	
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
	 * The origin address of this message.
	 */
	protected String origin;
	
	/**
	 * The person that sent this message
	 */
	protected Person sender;
	
	/**
	 * A list of MessagingAddresses representing the recipients of this message.
	 * MessagingAddresses do not necessarily have a non-null person field.
	 */
	protected Set<MessagingAddress> to;
	
	/**
	 * The message that this message is replying to.
	 */
	private Message inReplyTo;
	
	/**
	 * The date that the message was sent or received. Intentionally left a little ambiguous.
	 */
	protected Date date;

	/**
	 * The status of this message
	 */
	protected Integer status;
	
	/**
	 * The number of times that the system has tried to send the message. Once
	 * this number reaches the max_retries global property value, the message
	 * will be marked as 'failed' and the system will no longer try to
	 * send it.
	 */
	private Integer sendAttempts = 0;

	/**
	 * The Id of the protocol associated with this message
	 */
	private String protocolClass;

	public Message(String to, String content){
		this.setTo(new HashSet<MessagingAddress>());
		getTo().add(new MessagingAddress(to,null));
		this.content = content;
	}
	/**
	 * Creates a message with only a destination and content. The date and
	 * origin are filled in automatically at the time of sending.
	 * 
	 * @param content
	 *            the content of the message
	 */
	public Message(MessagingAddress to, String content) {
		this.setTo(new HashSet<MessagingAddress>());
		getTo().add(to);
		this.content = content;
	}

	/**
	 * Creates a messaging address with 1 destination and an origin. If the message is sent 
	 * from a gateway with only 1 exit point from OpenMRS (like email or SMS), the 'from' address
	 * may be overwritten.
	 * @param to
	 * @param from
	 * @param content
	 */
	public Message(MessagingAddress to, MessagingAddress from, String content) {
		this.setTo(new HashSet<MessagingAddress>());
		getTo().add(to);
		this.setOrigin(from.getAddress());
		this.setSender(from.getPerson());
		this.content = content;
	}
	
	/**
	 * Creates a messaging address with several destinations and an origin. If the message is sent 
	 * from a gateway with only 1 exit point from OpenMRS (like email or SMS), the 'from' address
	 * may be overwritten.
	 * @param to
	 * @param from
	 * @param content
	 */
	public Message(Set<MessagingAddress> to, MessagingAddress from, String content) {
		this.setTo(to);
		this.setOrigin(from.getAddress());
		this.setSender(from.getPerson());
		this.content = content;
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

	@SuppressWarnings("unchecked")
	public Class<? extends Protocol> getProtocol() {
		try {
			return (Class<? extends Protocol>) Class.forName(getProtocolClass());
		} catch (ClassNotFoundException e) {
			log.error("Message Protocol class is not correct",e);
			return null;
		}
	}

	public void setProtocol(Class<? extends Protocol> protocolClass) {
		this.setProtocolClass(protocolClass.getName());
	}

	private Integer getStatus() {
		return status;
	}
	
	/**
	 * @return The message status, in the {@link MessageStatus} enum.
	 */
	public MessageStatus getMessageStatus() {
		return MessageStatus.getStatusByNumber(this.getStatus());
	}

	public void setStatus(MessageStatus status) {
		this.status = status.getNumber();
	}

	public Integer getSendAttempts() {
		return sendAttempts;
	}
	
	public void setSendAttempts(Integer sendAttempts) {
		this.sendAttempts = sendAttempts;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public void setTo(Set<MessagingAddress> to) {
		this.to = to;
	}
	
	public boolean addDestination(MessagingAddress destination){
		if(destination == null){
			return false;
		}else{
			return to.add(destination);
		}	
	}

	/**
	 * @return the recipients
	 */
	public Set<MessagingAddress> getTo() {
		return to;
	}
	
	public Set<String> getToAddresses(){
		Set<String> addresses = new HashSet<String>();
		for(MessagingAddress address: to){
			if(address.getAddress() != null){
				addresses.add(address.getAddress());
			}
		}
		return addresses;
	}
	
	public Set<Person> getToPeople(){
		Set<Person> people = new HashSet<Person>();
		for(MessagingAddress address: to){
			if(address.getPerson() != null){
				people.add(address.getPerson());
			}
		}
		return people;
	}
	
	public String getDisplayOrigin(){
		if(getSender() != null){
			return getSender().getPersonName().toString();
		}else if(getOrigin() != null && !getOrigin().equals("")){
			return getOrigin();
		}else{
			return "";
		}
	}
	
	public String getDisplayDestination(){
		if(to != null){
			StringBuilder destination = new StringBuilder();
			for(MessagingAddress address: to){
				if(address.getPerson() != null){
					destination.append(address.getPerson().getPersonName().toString()+ " ");
				}
				destination.append("<"+address.getAddress()+">");
			}
			return destination.toString();
		}
		return "";
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
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
	
	/**
	 * @param protocolClass the protocolClass to set
	 */
	private void setProtocolClass(String protocolClass) {
		this.protocolClass = protocolClass;
	}
	/**
	 * @return the protocolClass
	 */
	private String getProtocolClass() {
		return protocolClass;
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

	public enum MessageFields{
		STATUS("status"),
		DATE("date"),
		IN_REPLY_TO("inReplyTo"),
		TO("to"),
		SENDER("sender"),
		ORIGIN("origin"),
		SUBJECT("subject"),
		CONTENT("content"),
		MESSAGE_ID("messageId"),
		PROTOCOL_CLASS("protocolClass");
		
		public final String fieldName;
		private MessageFields(String name){ this.fieldName = name; }
		public String toString(){ return fieldName; }
	}
}
