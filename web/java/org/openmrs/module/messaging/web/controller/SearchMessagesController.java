package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SearchMessagesController {

	@RequestMapping(value = "/module/messaging/search_messages")
	public void populateModel(HttpServletRequest request){
		
	}
	
	@RequestMapping(value="/module/messaging/search", method=RequestMethod.POST)
	public ModelAndView search(@RequestParam("searchString") String searchString, 
							   @RequestParam("searchingInbox") boolean searchingInbox,
							   @RequestParam("searchingSent") boolean searchingSent,
							   HttpServletRequest request){
		HttpSession session = request.getSession();
		session.setAttribute("searchString", searchString);
		session.setAttribute("searchingInbox", searchingInbox);
		session.setAttribute("searchingSent", searchingSent);
		return new ModelAndView(new RedirectView("search_messages.form"));
	}
}
