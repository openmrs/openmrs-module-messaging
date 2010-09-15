package org.openmrs.module.messaging.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.web.controller.PortletController;

public class MessagingDashboardTabController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		request.setAttribute("protocols", Context.getService(MessagingService.class).getProtocols());
		Patient p = (Patient) model.get("patient");
		request.setAttribute("patientAddresses", Context.getService(MessagingAddressService.class).getMessagingAddressesForPerson(p));
	}
}
