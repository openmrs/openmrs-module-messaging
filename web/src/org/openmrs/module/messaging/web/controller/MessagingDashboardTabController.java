package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.AddressSet;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.web.controller.PortletController;

public class MessagingDashboardTabController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		//retrieve the patient that the tab is for
		Patient p = (Patient) model.get("patient");
		//put all that patient's addresses in the model
		MessagingAddressService addressService = Context.getService(MessagingAddressService.class);
		List<MessagingAddress> addresses = addressService.getMessagingAddressesForPerson(p);
		model.put("messagingAddresses", addresses);
		//put all the messages to or from that person in the model
		List<Message> messages =  Context.getService(MessageService.class).getMessagesToOrFromPerson(p);
		model.put("messages", messages);
		
		//put the service names and the addresses that can be sent to in the model
		List<AddressSet> fromAddresses = new ArrayList<AddressSet>();
		for (MessagingGateway gateway : MessagingService.getInstance().getActiveMessagingGateways()) {
				AddressSet s = new AddressSet(gateway.getName());
				s.addAddress(gateway.getDefaultSenderAddress().getAddress());
				if(gateway.canSendFromUserAddresses()){
					List<MessagingAddress> faddresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(Context.getAuthenticatedUser().getPerson(), gateway);
					for(MessagingAddress a:faddresses){
						s.addAddress(a.getAddress());
					}
				}
				fromAddresses.add(s);
		}
		model.put("fromAddresses", fromAddresses);
		List<AddressSet> toAddresses = new ArrayList<AddressSet>();
		for (MessagingGateway gateway : MessagingService.getInstance().getActiveMessagingGateways()) {
			AddressSet s = new AddressSet(gateway.getName());
			List<MessagingAddress> faddresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(p, gateway);
			for(MessagingAddress a:faddresses){
				s.addAddress(a.getAddress());
			}
			toAddresses.add(s);
		}
		model.put("toAddresses", toAddresses);
		
		model.put("address_types", MessagingService.getInstance().getAddressTypes());
	}
}
