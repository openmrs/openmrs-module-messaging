package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.web.WebConstants;
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
		wdb.registerCustomEditor(Person.class, new PersonEditor());
	}

	@RequestMapping(value = "/module/messaging/addAddress", method = RequestMethod.POST)
	public String addAddress(
			@RequestParam(value="person", required=false) Person person,
			@RequestParam("address_type") String addressType,
			@RequestParam("address") String address,
			@RequestParam(value="password", required=false) String password,
			@RequestParam(value="preferred", required=false) Boolean preferred,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request) {

		MessagingAddress a = null;
		if(person == null){
			person = Context.getAuthenticatedUser().getPerson();
		}
		try {
			a = MessagingService.getInstance().getAddressFactoryForAddressTypeName(addressType).createAddress(address, person);
			if(password != null){
				a.setPassword(password);
			}
			if(preferred != null){
				a.setPreferred(preferred);
			}
		} catch (AddressFormattingException e) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getDescription());
		}
		if (a != null) {
			Context.getService(MessagingAddressService.class).saveMessagingAddress(a);
		}

		if (returnUrl == null)
			returnUrl = "messaging.form?patient_id=" + person.getPersonId();

		return "redirect:" + returnUrl;
	}
}
