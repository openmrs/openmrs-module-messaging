package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import winterwell.jtwitter.Twitter;

@Controller
public class ManageGatewaysController {

	@RequestMapping("/module/messaging/admin/manageGateways")
	public void populateModel(HttpServletRequest request){
//		request.setAttribute("twitterUsername", Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME));
//		request.setAttribute("twitterPassword", Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD));
//		String twitterStatus = MessagingService.getInstance().getMessagingGateway(TwitterGateway.class).canSend()?"Started":"Stopped";
//		request.setAttribute("twitterServiceStatus", twitterStatus);
//		List<ModemInfo> modems = MessagingService.getInstance().getMessagingGateway(SmsModemGateway.class).getActiveModems();
//		request.setAttribute("modems", modems);
	}
	
	@RequestMapping(value="/module/messaging/changeDefaultTwitterCreds", method=RequestMethod.POST)
	public ModelAndView changeDefaultTwitterCredentials(
			@RequestParam("username") String username,
			@RequestParam("password1") String password1,
			@RequestParam("password2") String password2,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request){
		
		HttpSession httpSession = request.getSession();
		if(!password1.equals(password2)){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The passwords didn't match");
		}else{
			if(new Twitter(username,password1).isValidLogin()){
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME, username));
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD, password1));
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Credentials saved");
			}else{
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The credentials provided were not a valid twitter login");
			}
		}
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@RequestMapping("/module/messaging/detectModems")
	public String addAddress(@RequestParam(value = "returnUrl", required = false) String returnUrl) {
		
		//MessagingService.getInstance().getMessagingGateway(SmsModemGateway.class).detectModems();
		
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";

		return "redirect:" + returnUrl;
	}
}
