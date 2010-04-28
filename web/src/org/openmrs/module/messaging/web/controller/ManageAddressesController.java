package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.MessagingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManageAddressesController {

	@RequestMapping("/module/messaging/admin/manageAddresses")
	public void populateModel(HttpServletRequest request){
		if(Context.getAuthenticatedUser()!=null){
			List<MessagingAddress> addresses = Context.getService(MessagingAddressService.class)
												.getMessagingAddressesForPerson(Context.getAuthenticatedUser().getPerson());
			request.setAttribute("addresses", addresses);
			Set<MessagingGateway> gateways =  MessagingService.getInstance().getAllMessagingGateways();
			List<String> gatewayTitles = new ArrayList<String>();
			for(MessagingGateway gateway: gateways){
				gatewayTitles.add(gateway.getName());
			}
			request.setAttribute("addressTypes", MessagingService.getInstance().getAddressTypes());
		}
	
	}
}
