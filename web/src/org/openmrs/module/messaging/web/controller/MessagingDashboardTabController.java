package org.openmrs.module.messaging.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.web.controller.PortletController;

public class MessagingDashboardTabController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		MessagingService mService = MessagingService.getInstance();
		//retrieve the patient that the tab is for
		Patient p = (Patient) model.get("patient");
		//put all that patient's addresses in the model
		MessagingAddressService addressService = Context.getService(MessagingAddressService.class);
		Person currentPerson = Context.getAuthenticatedUser().getPerson();
		List<MessagingAddress> addresses = addressService.getMessagingAddressesForPerson(p);
		model.put("messagingAddresses", addresses);
		//put all the messages to or from that person in the model
		List<Message> messages =  Context.getService(MessageService.class).getMessagesToOrFromPerson(p);
		model.put("messages", messages);
		
		//create lists to contain the sets of addresses
	//	List<AddressSet> fromAddresses = new ArrayList<AddressSet>();
		//List<AddressSet> toAddresses = new ArrayList<AddressSet>();
		//iterate over all 'active' address factories (factories that have an associated gateway that can send and recieve
//		for (AddressFactory<?> factory : mService.getActiveAddressFactories()) {
//			//create the address sets for the factory
//			AddressSet fromSet = new AddressSet(factory.getAddressTypeName());
//			AddressSet toSet = new AddressSet(factory.getAddressTypeName());
//			boolean canSendFromUserAddresses = false;
//			//for each active gateway associated with the address factory, add the default address
//			//and store whether or not it can send from user addresses
//			for (MessagingGateway<?, ?> mg : mService.getActiveMessagingGatewaysForAddressClass(factory.getAddressClass())) {
//				if (mg.canSendFromUserAddresses()) {
//					canSendFromUserAddresses = true;
//				}
//				fromSet.addAddress(mg.getDefaultSenderAddress().getAddress());
//			}
//			//the there is a gateway that can send from user addresses, add that user's addresses
//			if (canSendFromUserAddresses) {
//				List<MessagingAddress> faddresses = addressService.getMessagingAddressesForPersonAndTypeName(currentPerson,factory.getAddressTypeName());
//				for (MessagingAddress a : faddresses) {
//					fromSet.addAddress(a.getAddress());
//				}
//			}
//			fromAddresses.add(fromSet);
//			//add the patient's addresses to the toAddress set
//			List<MessagingAddress> tAddresses = addressService.getMessagingAddressesForPersonAndTypeName(p,factory.getAddressTypeName());
//			for (MessagingAddress a : tAddresses) {
//				toSet.addAddress(a.getAddress());
//			}
//			toAddresses.add(toSet);
//		}
	//	model.put("fromAddresses", fromAddresses);
//		model.put("toAddresses", toAddresses);
	//	model.put("address_types", mService.getAddressTypeNames());
	}
}
