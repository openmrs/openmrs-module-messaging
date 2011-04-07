package org.openmrs.module.messaging.web.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;

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
	private boolean read;

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
		Iterator<MessageRecipient> itr = message.getTo().iterator();
		while(itr.hasNext()){
			MessageRecipient recipient = itr.next();
			recipients += recipient.getRecipient().getPerson().getPersonName().toString();
			if(recipient.getRecipient().getPerson().equals(Context.getAuthenticatedUser().getPerson())){
				this.read = recipient.isRead();
			}
			if(itr.hasNext()){
				recipients +=", ";
			}
		}
		setDateAndTime(message.getDate());
		//get a set of all the protocols
		Set<String> protSet = new HashSet<String>();
		for(MessageRecipient mr: message.getTo()){
			protSet.add(Context.getService(MessagingService.class).getProtocolByClass(mr.getProtocol()).getProtocolName());
		}
		//build a string with the protocol set
		StringBuilder protocols = new StringBuilder();
		Iterator<String> protIterator = protSet.iterator();
		while(protIterator.hasNext()){
			if(protIterator.hasNext()){
				protocols.append(protIterator.next()+ ", ");
			}else{
				protocols.append(protIterator.next());
			}
		}
		this.setProtocolName(protocols.toString());
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
			this.date = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
			this.time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
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

	/**
	 * @param read the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * @return the read
	 */
	public boolean isRead() {
		return read;
	}
}
