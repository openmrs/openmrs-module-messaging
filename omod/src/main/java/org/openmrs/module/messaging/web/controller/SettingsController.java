package org.openmrs.module.messaging.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SettingsController {

	@RequestMapping(value = "/module/messaging/settings")
	public void populateModel(HttpServletRequest request){
		Map<String,String> protocols = new HashMap<String,String>();
		for(Protocol p:Context.getService(MessagingService.class).getProtocols()){
			protocols.put(p.getClass().getName(), p.getProtocolName());
		}
		request.setAttribute("protocolNames2", protocols);
	}
}
