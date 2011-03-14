package org.openmrs.module.messaging.web.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessagingAddress;

/**
 * MessageBean is a class for sending message objects over ajax. It's very
 * similar to a Message, except all data is in text form.
 * 
 * @author dieterichlawson
 * 
 */
public class MessageBean implements Serializable {

	private static final long serialVersionUID = 6998333130334839317L;

	private Integer id;
	private String content;
	private String subject;
	private String origin;
	private String sender;
	private String recipients;
	private String date;
	private String time;
	private String protocolName;

	public MessageBean() {
		id = -1;
	}

	public MessageBean(Message message) {
		this.id =message.getId();
		this.setContent(message.getContent());
		this.setSubject(message.getSubject());
		this.setOrigin(message.getOrigin());
		this.sender = message.getSender().getPersonName().toString();
		setRecipients("");
		for(MessagingAddress address: message.getTo()){
			setRecipients(getRecipients() + (address.toString()+ ", "));
		}
		setDateAndTime(message.getDate());
		this.setProtocolName(Context.getService(MessagingService.class).getProtocolByClass(message.getProtocol()).getProtocolName());
	}
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	public void setDateAndTime(Date date) {
		if (date != null) {
			this.date = DateFormat.getDateInstance(DateFormat.SHORT).format(
					date);
			this.time = DateFormat.getTimeInstance(DateFormat.SHORT).format(
					date);
		}
	}

	/**
	 * @param protocolName
	 *            the protocolName to set
	 */
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	/**
	 * @return the protocolName
	 */
	public String getProtocolName() {
		return protocolName;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
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
	 * @param recipients the recipients to set
	 */
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the recipients
	 */
	public String getRecipients() {
		return recipients;
	}
}
