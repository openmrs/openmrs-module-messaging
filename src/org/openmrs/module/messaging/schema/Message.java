package org.openmrs.module.messaging.schema;

import java.util.Date;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Person;

/**
 * A class that represents a text based message sent within the messaging
 * framework. This class should be extended to create different message types,
 * but could be used as-is in rare situations. In general, you should not call
 * the message constructor directly but should instead create them via a class
 * that implements the {@link MessageFactory} interface. Messages created
 * outside of message factories are not guaranteed to have properly formatted.
 * 
 * @see MessageFactory
 */
public abstract class Message extends BaseOpenmrsObject{

	protected Message(){}
	
	private Integer messageId;

	// content
	/**
	 * The string content of the message.
	 */
	protected String content;

	// header information
	/**
	 * The address that the message originated from
	 */
	protected String origin;

	/**
	 * The address that the message was sent/is being sent to
	 */
	protected String destination;
	
	/**
	 * The person that sent this message, can be null
	 */
	private Person sender;
	
	/**
	 * The person who received this message, can be null
	 */
	private Person recipient;

	/**
	 * The date that the message was sent
	 */
	protected Date dateSent;

	/**
	 * The date that this message was received
	 */
	protected Date dateReceived;

	/**
	 * The priority of this message. Priorities are currently
	 * not persisted in the database
	 */
	protected Integer priority;

	/**
	 * The status of this message
	 */
	protected Integer status;
	
	/**
	 * The string Id of the gateway that sent this message
	 */
	private String gatewayId;

	/**
	 * Creates a message with only a destination and content. The date sent,
	 * date received, and origin are all filled in automatically at the time of
	 * sending. Origin is set to the relevant address of the currently
	 * authenticated user.
	 * 
	 * @param destination
	 *            the destination of the message
	 * @param content
	 *            the content of the message
	 */
	public Message(String destination, String content) {
		this.destination= destination;
		this.content = content;
	}

	/**
	 * Creates a message with a destination, origin, priority, and content. The date
	 * sent, date received, and origin are all filled in automatically at the
	 * time of sending. Origin is set to the relevant address of the currently
	 * authenticated user
	 * 
	 * @param destination
	 *            where the message is going
	 * @param content
	 *            the content of the message
	 */
	public Message(String destination, String origin, String content, int priority) {
		this.destination = destination;
		this.origin =origin;
		this.content = content;
		this.priority = priority;
	}

	/**
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return
	 */
	public Date getDateSent() {
		return dateSent;
	}

	/**
	 * @param dateSent
	 */
	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	/**
	 * @return
	 */
	public Date getDateReceived() {
		return dateReceived;
	}

	/**
	 * @param dateReceived
	 */
	public void setDateReceived(Date dateRecieved) {
		this.dateReceived = dateRecieved;
	}

	/**
	 * @return
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getMessageId() {
		return messageId;
	}
	
	public void setId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getId() {
		return messageId;
	}

	public void setSender(Person sender) {
		this.sender = sender;
	}

	public Person getSender() {
		return sender;
	}

	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

	public Person getRecipient() {
		return recipient;
	}
	
	/**
	 * @return The sender's name if the sender is set. Otherwise it returns the
	 *         address of origin
	 */
	public String getDisplayOrigin() {
		if (sender != null) {
			return sender.getPersonName().toString();
		} else {
			return origin;
		}
	}

	/**
	 * @return The recipient's name if the recipient is set. Otherwise it
	 *         returns the destination address
	 */
	public String getDisplayDestination() {
		if (recipient != null) {
			return recipient.getPersonName().toString();
		} else {
			return destination;
		}
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getGatewayId() {
		return gatewayId;
	}

}
