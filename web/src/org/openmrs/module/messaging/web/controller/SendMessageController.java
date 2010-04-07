package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageFormattingException;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingCenter;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SendMessageController extends SimpleFormController{

	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj, BindException errors) throws Exception {
		MessagingService ms = (MessagingService) MessagingCenter.getMessagingServiceForName(request.getParameter("service"));
		MessagingAddress address = null;
		Message message = null;
		String view = getSuccessView();
		//check the validity of the address
		try{
			address = ms.getAddressFactory().createAddress(request.getParameter("address"), null);
		}catch(AddressFormattingException e){
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getDescription());
			return new ModelAndView(new RedirectView(view));
		}
		//check the validity of the message
		try{
			message = ms.getMessageFactory().createMessage(request.getParameter("content"), null, address);
		}catch(MessageFormattingException e){
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getDescription());
			return new ModelAndView(new RedirectView(view));
		}
		
		if(!ms.canSend()){
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The "+ms.getName()+" service is not currently running");
			return new ModelAndView(new RedirectView(view));
		}else{
			ms.sendMessage(request.getParameter("address"), request.getParameter("content"));
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "DM sent successfully!");
			return new ModelAndView(new RedirectView(view));
		}
	
	}
	
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
    	ArrayList<String> services = new ArrayList<String>();
    	for(MessagingService ms: MessagingCenter.getAllMessagingServices()){
    		services.add(ms.getName());
    	}
    	return services;
    }
}
