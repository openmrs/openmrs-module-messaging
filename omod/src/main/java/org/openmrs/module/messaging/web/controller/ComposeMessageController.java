package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ComposeMessageController {
	
	
	@RequestMapping(value = "/module/messaging/compose_message")
	public void populateModel(HttpServletRequest request){
		
	}
}
