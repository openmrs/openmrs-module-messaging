package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.web.domain.AddressAutocompleteBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ComposeMessageController {
	
	
	@RequestMapping(value = "/module/messaging/compose_message")
	public void populateModel(HttpServletRequest request){
		
	}
}
