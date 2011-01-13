package org.openmrs.module.messaging.web.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;

/**
 * MessageBean is a class for sending message objects over ajax. It's very
 * similar to a Message, except all data is in text form and there is an
 * additional 'colornumber' that specifies what color the sender's name should
 * be.
 * 
 * @author dieterichlawson
 * 
 */
public class MessageBean implements Serializable {

	private static final long serialVersionUID = 6998333130334839317L;

	private String text;
	private String sender;
	private boolean fromOpenMRS;
	private int colorNumber;
	private String date;
	private String time;
	private Integer id;
	private String protocolName;

	public MessageBean() {
		id = -1;
	}

	public MessageBean(Message message) {
		this.setId(message.getId());
		this.text = message.getContent();
		this.sender = message.getDisplayOrigin();
		this.setProtocolName(Context.getService(MessagingService.class)
				.getProtocolById(message.getProtocolId()).getProtocolName());
		if (message.getDate() != null) {
			this.date = DateFormat.getDateInstance(DateFormat.SHORT).format(
					message.getDate());
			this.time = DateFormat.getTimeInstance(DateFormat.SHORT).format(
					message.getDate());
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public boolean isFromOpenMRS() {
		return fromOpenMRS;
	}

	public void setFromOpenMRS(boolean isFromPatient) {
		this.fromOpenMRS = isFromPatient;
	}

	public int getColorNumber() {
		return colorNumber;
	}

	public void setColorNumber(int colorNumber) {
		this.colorNumber = colorNumber;
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

}
