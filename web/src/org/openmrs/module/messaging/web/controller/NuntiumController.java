package org.openmrs.module.messaging.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageStatus;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class NuntiumController {
	
	@RequestMapping("/module/messaging/nuntium/incoming")
	public void incoming(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!authorized(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		MessageService messageService = Context.getService(MessageService.class);
		MessagingService messagingService = Context.getService(MessagingService.class);
		
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String subject = request.getParameter("subject");
		String body = request.getParameter("body");
		String content = subject;
		if (subject == null) {
			content = body;
		} else {
			if (body != null) {
				content = subject + " - " + body;
			}
		}
		
		if (content == null) {
			content = "";
		}
		
		String[] fromSplitted = splitProtocolAndAddress(from);
		String[] toSplitted = splitProtocolAndAddress(from);
		
		String fromProtocol;
		String toProtocol;
		
		fromProtocol = fromSplitted[0];
		from = fromSplitted[1];
		toProtocol = toSplitted[0];
		to = toSplitted[1];
		
		String protocol = null;
		for(Protocol proc : messagingService.getProtocols()) {
			if (proc.getProtocolId().equals(toProtocol) || proc.getProtocolId().equals(fromProtocol)) {
				protocol = proc.getProtocolId();
				break;
			}
		}
		
		if (protocol == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Context.openSession();
		Message m = new Message(to, from, content);
		m.setMessageStatus(MessageStatus.RECEIVED);
		m.setDate(new Date());
		m.setProtocolId(protocol);
		messageService.saveMessage(m);
		Context.closeSession();
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private boolean authorized(HttpServletRequest request) throws Exception {
		AdministrationService admin = Context.getAdministrationService();
		
		String realUser = admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_CALLBACK_USER, "");
		String realPassword = admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_CALLBACK_PASSWORD, "");
		
		if (realUser.length() == 0 || realPassword.length() == 0)
			return true;
		
		String auth = request.getHeader("authorization");
		if (auth == null || !auth.startsWith("Basic "))
			return false;
		
		auth = auth.substring(6);
		auth = new String(new sun.misc.BASE64Decoder().decodeBuffer(auth));
		int index = auth.indexOf(':');
		if (index < 0)
			return false;
		
		String user = auth.substring(0, index);
		String password = auth.substring(index + 1);
		
		if (!user.equals(realUser) || !password.equals(realPassword))
			return false;
		
		return true;
	}
	
	private String[] splitProtocolAndAddress(String address) {
		if (address == null) {
			return new String[] { "", "" };
		}
		int index = address.indexOf("://");
		if (index < 0) {
			return new String[] { "", "" };
		}
		
		return new String[] { address.substring(0, index), address.substring(index + 3) };
	}

}
