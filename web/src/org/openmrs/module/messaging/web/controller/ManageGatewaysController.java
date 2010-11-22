package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.googlevoice.GoogleVoiceGateway;
import org.openmrs.module.messaging.sms.SmsLibGateway;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.techventus.server.voice.Voice;

@Controller
public class ManageGatewaysController {

	private GatewayManager gatewayManager;
	
	/**
	 * For Spring only
	 * @param manager
	 */
	public void setGatewayManager(GatewayManager manager){
		this.gatewayManager = manager;
	}
	
	@RequestMapping("/module/messaging/admin/manageGateways")
	public void populateModel(HttpServletRequest request){
		if(gatewayManager.getGatewayByClass(GoogleVoiceGateway.class) != null){
			request.setAttribute("googleVoiceUsername", Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME));
			request.setAttribute("googleVoicePassword", Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD));
			String gvStatus = gatewayManager.getGatewayByClass(GoogleVoiceGateway.class).isActive()? "Active":"Inactive";
			request.setAttribute("googleVoiceStatus", gvStatus);
		}
	}
	
	@RequestMapping(value="/module/messaging/changeGoogleVoiceCreds", method=RequestMethod.POST)
	public ModelAndView changeGoogleVoiceCredentials(
			@RequestParam("username") String username,
			@RequestParam("password1") String password1,
			@RequestParam("password2") String password2,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request){
		
		HttpSession httpSession = request.getSession();
		if(!password1.equals(password2)){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The passwords didn't match");
		}else{
			try {
				//see if the login is valid
				new Voice(username,password1);
				//save the global properties
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME, username));
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD, password1));
				//update the gateway itself
				gatewayManager.getGatewayByClass(GoogleVoiceGateway.class).updateCredentials(username, password1);
				//tell the user it worked
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Credentials saved");
			} catch (Exception e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The credentials provided were not a valid Google Voice login");
			}
		}
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@RequestMapping("/module/messaging/detectModems")
	public String addAddress(@RequestParam(value = "returnUrl", required = false) String returnUrl) {
		
		gatewayManager.getGatewayByClass(SmsLibGateway.class).startup();
		
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";

		return "redirect:" + returnUrl;
	}
}
