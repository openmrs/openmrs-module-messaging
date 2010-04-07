package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingCenter;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.web.controller.PortletController;

public class MessagingDashboardTabController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Patient p = (Patient) model.get("patient");
		try {
			MessagingAddressService addressService = (MessagingAddressService) Context.getService(MessagingAddressService.class);
			List<MessagingAddress> addresses = addressService.getMessagingAddressesForPerson(p);
			List<Message> messages = ((MessageService) Context .getService(MessageService.class)).getMessagesToOrFromPerson(p);
			List<MessagingService> services = MessagingCenter.getAllMessagingServices();
			model.put("messagingAddresses", addresses);
			model.put("messages", messages);
			model.put("services", services);
			List<String> serviceTitles = new ArrayList<String>();
			for (MessagingService service : services) {
				serviceTitles.add(service.getName());
			}
			model.put("serviceTitles", serviceTitles);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
