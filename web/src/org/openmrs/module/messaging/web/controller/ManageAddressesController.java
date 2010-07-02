package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManageAddressesController {

	@RequestMapping("/module/messaging/admin/manageAddresses")
	public void populateModel(HttpServletRequest request){
			Set<Protocol> protocols=  MessagingService.getProtocols();
			List<String> protocolTitles = new ArrayList<String>();
			for(Protocol p: protocols){
				protocolTitles.add(p.getProtocolId());
			}
			request.setAttribute("protocols", protocolTitles);
	}
}
