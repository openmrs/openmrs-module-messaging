package org.openmrs.module.messaging.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.domain.Message;

/**
 * This tag prints out the to or from string for a message
 * accepts: message or messageId
 * prints out: Sender/Recipient if not null, Origin/Destination otherwise
 *
 */
public class FormatAddressTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());
	
	private Message message = null;
	private Integer messageId = null;
	private String toOrFrom = "to";

	public int doStartTag() throws JspException{
		if(messageId != null){
			message = Context.getService(MessageService.class).getMessage(messageId);
		}
		if(message != null){
			if(toOrFrom.equals("to")){
				if(message.getRecipient() != null){
					try {
						pageContext.getOut().write(message.getRecipient().getPersonName().toString());
					} catch (IOException e) {
						log.error("Unable to write recipient to output", e);
					}
				}else if(message.getDestination() !=null){
					try {
						pageContext.getOut().write(message.getDestination());
					} catch (IOException e) {
						log.error("Unable to write destination to output", e);
					}
				}
			}else if(toOrFrom.equals("from")){
				if(message.getSender() != null){
					try {
						pageContext.getOut().write(message.getSender().getPersonName().toString());
					} catch (IOException e) {
						log.error("Unable to write sender to output", e);
					}
				}else if(message.getOrigin() !=null){
					try {
						pageContext.getOut().write(message.getOrigin());
					} catch (IOException e) {
						log.error("Unable to write origin to output", e);
					}
				}
			}
		}
		reset();
		return SKIP_BODY;
	}
	
	public void reset(){
		message = null;
		messageId = null;
		toOrFrom="to";
	}
	public void setToOrFrom(String toOrFrom) {
		this.toOrFrom = toOrFrom;
	}

	public String getToOrFrom() {
		return toOrFrom;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
