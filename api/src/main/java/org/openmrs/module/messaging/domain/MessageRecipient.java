package org.openmrs.module.messaging.domain;

import org.openmrs.BaseOpenmrsObject;

public class MessageRecipient extends BaseOpenmrsObject{

	protected MessageRecipient(){}
	
	private Integer messageRecipientId;
	private Message message;
	private MessagingAddress recipient;
	private boolean read;
	
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
}
