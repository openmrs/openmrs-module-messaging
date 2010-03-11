package org.openmrs.module.messaging.framework;

import java.util.Date;

import org.openmrs.Person;

/**
 * An class that represents a text based message sent within the messaging
 * framework. This class should be extended to create different message types,
 * but could be used as-is in rare situations. In general, you should not call
 * the message constructor directly but should instead create them via a class
 * that implements the {@link MessageFactory} interface. Messages created
 * outside of message factories are not guaranteed to have properly formatted
 * content or be initialized.
 */
public class Message {

	/** The string content of the message. */
	protected String content;

	/** The person that sent this message. This field is optional */
	protected Person sender;

	/** The recipient of this message. This field is optional */
	protected Person recipient;

	/** The address that the message originated from */
	protected String origin;

	/** The address that the message was sent/is being sent to */
	protected String destination;

	/** The date that the message was sent */
	protected Date dateSent;

	/** The date that this message was received */
	protected Date dateRecieved;

	/** The priority of this message */
	protected Integer priority;

	/** The status of this message */
	protected Integer status;

	/**
	 * Creates a message with only a destination and content. The date sent,
	 * date received, and origin are all filled in automatically at the time of
	 * sending. Origin is set to the relevant address of the currently
	 * authenticated user
	 * 
	 * @param destination
	 *            the destination of the message
	 * @param content
	 *            the content of the message
	 */
	public Message(String destination, String content) {
		this.destination = destination;
		this.content = content;
	}

	/**
	 * Creates a message with a destination, priority, and content. The date
	 * sent, date received, and origin are all filled in automatically at the
	 * time of sending. Origin is set to the relevant address of the currently
	 * authenticated user
	 * 
	 * @param destination
	 *            where the message is going
	 * @param content
	 *            the content of the message
	 */
	public Message(String destination, String content, int priority) {
		this.destination = destination;
		this.content = content;
		this.priority = priority;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	public Date getDateRecieved() {
		return dateRecieved;
	}

	public void setDateRecieved(Date dateRecieved) {
		this.dateRecieved = dateRecieved;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @param sender
	 *            the sender to set
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
	 * @param recipient
	 *            the recipient to set
	 */
	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return the recipient
	 */
	public Person getRecipient() {
		return recipient;
	}

}
