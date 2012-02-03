package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SentMessagesController {

	@RequestMapping(value = "/module/messaging/sent_messages")
	public void populateModel(HttpServletRequest request){
		
	}
}
