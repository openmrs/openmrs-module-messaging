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

	private boolean read;
	
	private Date date = new Date();
	
	public MessageRecipient(MessagingAddress recipient, Message message) {
		super();
		this.message = message;
		this.recipient = recipient;
		this.read = false;
	}
	
	public MessageRecipient(MessagingAddress recipient, boolean read, Message message) {
		super();
		this.message = message;
		this.recipient = recipient;
		this.read = read;
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
	

	@SuppressWarnings("unchecked")
	public Class<? extends Protocol> getProtocol() {
		try {
			return (Class<? extends Protocol>) Class.forName(getProtocolClass());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void setProtocol(Class<? extends Protocol> protocolClass) {
		this.setProtocolClass(protocolClass.getName());
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


	public enum MessageRecipientFields{
		MESSAGE_RECIPIENT_ID("messageRecipientId"),
		MESSAGE("message"),
		RECIPIENT("recipient"),
		SEND_ATTEMPTS("sendAttempts"),
		READ("read"),
		STATUS("status"),
		DATE("date"),
		PROTOCOL_CLASS("protocolClass");

		public final String name;
		private MessageRecipientFields(String name){ this.name = name; }
		public String toString(){ return name; }
	}
}
