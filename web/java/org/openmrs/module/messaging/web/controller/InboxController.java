package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.omail.OMailProtocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InboxController {

	@RequestMapping(value = "/module/messaging/inbox")
	public void populateModel(HttpServletRequest request){
		Person p = Context.getAuthenticatedUser().getPerson();
		int messageNumber = Context.getService(MessageService.class).getMessagesForPerson(p, true, OMailProtocol.class).size();
		request.setAttribute("messageNumber", messageNumber);
	}
}
