package org.openmrs.module.messaging.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.MessagingService;

public class MessagingServiceTag extends TagSupport {

	public static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

	private Message message = null;
	private Integer messageId = null;
	private MessagingGateway service = null;

	public int doStartTag() throws JspException {
		if (messageId != null) {
			message = Context.getService(MessageService.class).getMessage(messageId);
		}
		if (message != null) {
//			String name = MessagingService.getInstance().getMessagingGatewayForId(message.getGatewayId()).getName();
//			try {
//				pageContext.getOut().write(name);
//			} catch (IOException e) {
//				log.error("Unable to print message's service name to output",e);
//			}

		}
		if(service != null){
			try {
				pageContext.getOut().write(service.getName());
			} catch (IOException e) {
				log.error("Unable to print service's name to output",e);
			}
		}
		reset();
		return SKIP_BODY;
	}

	private void reset() {
		message = null;
		messageId = null;
		service = null;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public void setService(MessagingGateway service) {
		this.service = service;
	}

	public MessagingGateway getService() {
		return service;
	}

}
