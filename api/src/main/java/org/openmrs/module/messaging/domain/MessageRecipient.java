package org.openmrs.module.messaging.domain;

import java.util.Date;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public class MessageRecipient extends BaseOpenmrsObject{

	protected MessageRecipient(){}
	
	private Integer messageRecipientId;
	
	private Message message;
	
	private MessagingAddress recipient;
	
	/**
	 * The origin address of this message.
	 */
	protected String origin;
	
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

	private boolean read = false;
	
	private Date date = new Date();
	
	public MessageRecipient(MessagingAddress destination, Message message) {
		super();
		this.message = message;
		this.recipient = destination;
	}
	
	public MessagingAddress getRecipient() {
		return recipient;
	}
	public void setRecipient(MessagingAddress recipient) {
		this.recipient = recipient;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}

	public Integer getId() {
		return getMessageRecipientId();
	}

	public void setId(Integer id) {
		this.setMessageRecipientId(id);
	}

	/**
	 * @param messageRecipientId the messageRecipientId to set
	 */
	private void setMessageRecipientId(Integer messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
	}

	/**
	 * @return the messageRecipientId
	 */
	private Integer getMessageRecipientId() {
		return messageRecipientId;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}
	

	private Integer getStatus() {
		return status;
	}
	
	private void setStatus(Integer status){
		this.status = status;
	}
	
	/**
	 * @return The message status, in the {@link MessageStatus} enum.
	 */
	public MessageStatus getMessageStatus() {
		return MessageStatus.getStatusByNumber(this.getStatus());
	}

	public void setMessageStatus(MessageStatus status) {
		this.status = status.getNumber();
	}

	public Integer getSendAttempts() {
		return sendAttempts;
	}
	
	public void setSendAttempts(Integer sendAttempts) {
		this.sendAttempts = sendAttempts;
	}
	
	/**
	 * @return the protocolClass
	 */
	private String getProtocolClass() {
		return getRecipient().getProtocolClass();
	}
	

	@SuppressWarnings("unchecked")
	public Class<? extends Protocol> getProtocol() {
		try {
			return (Class<? extends Protocol>) Class.forName(getProtocolClass());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
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

	public enum MessageRecipientFields{
		MESSAGE_RECIPIENT_ID("messageRecipientId"),
		MESSAGE("message"),
		RECIPIENT("recipient"),
		SEND_ATTEMPTS("sendAttempts"),
		ORIGIN("origin"),
		READ("read"),
		STATUS("status"),
		DATE("date");
		
		public final String name;
		private MessageRecipientFields(String name){ this.name = name; }
		public String toString(){ return name; }
	}
}