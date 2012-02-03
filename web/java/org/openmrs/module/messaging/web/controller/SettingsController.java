package org.openmrs.module.messaging.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.PersonAttributeService;
import org.openmrs.module.messaging.domain.MessagingAddress;
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
		PersonAttributeService attributeService = Context.getService(PersonAttributeService.class);
		PersonService personService = Context.getPersonService();
		Person p = Context.getAuthenticatedUser().getPerson();
		request.setAttribute("protocolNames2", protocols);
		PersonAttribute shouldAlertAttr = attributeService.getPersonAttribute(p,personService.getPersonAttributeTypeByName(MessagingModuleActivator.SEND_OMAIL_ALERTS_ATTR_NAME));
		if(shouldAlertAttr != null){
			boolean shouldAlert = Boolean.parseBoolean(shouldAlertAttr.getValue());
			request.setAttribute("shouldAlert", shouldAlert);
		}
		PersonAttribute alertAddressAttr = attributeService.getPersonAttribute(p,personService.getPersonAttributeTypeByName(MessagingModuleActivator.ALERT_ADDRESS_ATTR_NAME));
		if(alertAddressAttr != null){
			int addressId = Integer.parseInt(alertAddressAttr.getValue());
			MessagingAddress alertAddress = Context.getService(MessagingAddressService.class).getMessagingAddress(addressId);
			request.setAttribute("alertAddress", alertAddress);
		}
	}
}
