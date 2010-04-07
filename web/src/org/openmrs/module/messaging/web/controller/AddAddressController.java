package org.openmrs.module.messaging.web.controller;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingCenter;
import org.openmrs.module.messaging.sms.SMSMessage;
import org.openmrs.propertyeditor.PatientEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddAddressController {

	@InitBinder
	public void initBinder(WebDataBinder wdb) {
		wdb.registerCustomEditor(Patient.class, new PatientEditor());
	}

	@RequestMapping(value = "/module/messaging/addAddress", method = RequestMethod.POST)
	public String addAddress(
			@RequestParam("patient_id") Patient patient,
			@RequestParam("service") String service,
			@RequestParam("address") String address,
			@RequestParam(value = "returnUrl", required = false) String returnUrl) {

		MessagingAddress a = null;
		try {
			a = (MessagingAddress) MessagingCenter.getMessagingServiceForName(service).getAddressFactory().createAddress(address, patient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (a != null) {
			((MessagingAddressService) Context.getService(MessagingAddressService.class)).saveMessagingAddress(a);
		}

		try {
			SMSMessage mess = new SMSMessage("+18064702422","Hello, testing this out");
			((MessageService) Context.getService(MessageService.class)).saveMessage(mess);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (returnUrl == null)
			returnUrl = "messaging.form?patient_id=" + patient.getPatientId();

		return "redirect:" + returnUrl;
	}
}
