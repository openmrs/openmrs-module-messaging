package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.googlevoice.GoogleVoiceGateway;
import org.openmrs.module.messaging.nuntium.Nuntium;
import org.openmrs.module.messaging.nuntium.NuntiumGateway;
import org.openmrs.module.messaging.nuntium.Nuntium.CredentialsCheckResult;
import org.openmrs.module.messaging.sms.SmsLibGateway;
import org.openmrs.module.messaging.twitter.TwitterGateway;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import winterwell.jtwitter.Twitter;

import com.techventus.server.voice.Voice;

@Controller
public class ManageGatewaysController {

	@RequestMapping("/module/messaging/admin/manageGateways")
	public void populateModel(HttpServletRequest request){
		AdministrationService admin = Context.getAdministrationService();
		
		String nuntiumUrl = admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_URL);
		if (nuntiumUrl == null)
			nuntiumUrl = "http://nuntium.instedd.org";
		boolean nuntiumEnabled = "yes".equals(admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ENABLED, "no"));
		request.setAttribute("nuntiumEnabled", nuntiumEnabled);
		request.setAttribute("nuntiumEnabledChecked", nuntiumEnabled ? "checked=\"checked\"" : "");
		request.setAttribute("nuntiumDisabledChecked", !nuntiumEnabled ? "checked=\"checked\"" : "");
		request.setAttribute("nuntiumUrl", nuntiumUrl);
		request.setAttribute("nuntiumAccount", admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ACCOUNT));
		request.setAttribute("nuntiumApplication", admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_APPLICATION));
		request.setAttribute("nuntiumCallbackUser", admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_CALLBACK_USER));
		request.setAttribute("nuntiumCallbackUrl", getNuntiumCallbackUrl(request));
		
		request.setAttribute("twitterUsername", admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME));
		request.setAttribute("twitterPassword", admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD));
		String twitterStatus = MessagingModuleActivator.manager.getGatewayByClass(TwitterGateway.class).isActive()? "Active":"Inactive";
		request.setAttribute("twitterStatus", twitterStatus);
		request.setAttribute("googleVoiceUsername", admin.getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME));
		request.setAttribute("googleVoicePassword", admin.getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD));
		String gvStatus = MessagingModuleActivator.manager.getGatewayByClass(GoogleVoiceGateway.class).isActive()? "Active":"Inactive";
		request.setAttribute("googleVoiceStatus", gvStatus);
	}

	@RequestMapping(value="/module/messaging/changeNuntiumCreds", method=RequestMethod.POST)
	public ModelAndView changeNuntiumCredentials(
			@RequestParam("enabled") String enabled,
			@RequestParam("url") String url,
			@RequestParam("account") String account,
			@RequestParam("application") String application,
			@RequestParam("password") String password,
			@RequestParam("callbackUser") String callbackUser,
			@RequestParam("callbackPassword") String callbackPassword,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request){
		
		boolean isEnabled = "yes".equals(enabled); 
		
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ENABLED, enabled));		
		if (isEnabled) {
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_URL, url));
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ACCOUNT, account));
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_APPLICATION, application));
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_PASSWORD, password));
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_CALLBACK_USER, callbackUser));
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_CALLBACK_PASSWORD, callbackPassword));
		}
		
		HttpSession httpSession = request.getSession();
		
		if (isEnabled) {
			Nuntium nuntium = new Nuntium(url, account, application, password);
			CredentialsCheckResult result = nuntium.checkCredentials();
			switch(result) {
			case InvalidCredentials:
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid nuntium credentials");
				break;
			case UnknownHost:
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Unknown host '" + url + "'");
				break;
			case Ok:
				MessagingModuleActivator.manager.getGatewayByClass(NuntiumGateway.class).setNuntium(nuntium);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Nuntium credentials saved");
				break;
			}
		} else {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Nuntium gateway disabled");
		}
		
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@RequestMapping(value="/module/messaging/changeTwitterCreds", method=RequestMethod.POST)
	public ModelAndView changeTwitterCredentials(
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
				//save the global properties
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_UNAME, username));
				Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(MessagingConstants.GP_DEFAULT_TWITTER_PASSWORD, password1));
				//update the gateway itself
				MessagingModuleActivator.manager.getGatewayByClass(TwitterGateway.class).updateCredentials(username, password1);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Credentials saved");
			}else{
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The credentials provided were not a valid twitter login");
			}
		}
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
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
				MessagingModuleActivator.manager.getGatewayByClass(GoogleVoiceGateway.class).updateCredentials(username, password1);
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
		
		MessagingModuleActivator.manager.getGatewayByClass(SmsLibGateway.class).startup();
		
		if (returnUrl == null)
			returnUrl = "admin/manageGateways.form";

		return "redirect:" + returnUrl;
	}
	
	private String getNuntiumCallbackUrl(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		int port = request.getServerPort();
		if (port != 80) {
			sb.append(":");
			sb.append(port);
		}
		sb.append("/");
		String contextPath = request.getContextPath();
		if (contextPath != null && contextPath.length() > 0) {
			sb.append(contextPath).append("/");
		}
		sb.append("module/messaging/nuntium/incoming.form");
		return sb.toString();
	}
}
