package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManageAddressesController {

	@RequestMapping("/module/messaging/admin/manageAddresses")
	public void populateModel(HttpServletRequest request){
			List<Protocol> protocols=  Context.getService(MessagingService.class).getProtocols();
			List<String> protocolTitles = new ArrayList<String>();
			for(Protocol p: protocols){
				protocolTitles.add(p.getProtocolId());
			}
			request.setAttribute("protocols", protocolTitles);
	}
}
