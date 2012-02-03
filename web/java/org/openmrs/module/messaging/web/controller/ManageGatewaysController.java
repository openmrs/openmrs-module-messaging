package org.openmrs.module.messaging.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.EncryptionService;
import org.openmrs.module.messaging.domain.gateway.GatewayManager;
import org.openmrs.module.messaging.email.EmailGateway;
import org.openmrs.module.messaging.googlevoice.GoogleVoiceGateway;
import org.openmrs.module.messaging.omail.OMailGateway;
import org.openmrs.module.messaging.sms.SmsLibGateway;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.util.OpenmrsUtil;
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
	
	@RequestMapping("/module/messaging/manage_gateways")
	public void populateModel(HttpServletRequest request){
		AdministrationService adminService = Context.getAdministrationService();
		
		if(gatewayManager.getGatewayByClass(GoogleVoiceGateway.class) != null){
			request.setAttribute("googleVoiceUsername", adminService.getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME));
			request.setAttribute("googleVoicePassword", adminService.getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD));
			boolean gvStatus = gatewayManager.getGatewayByClass(GoogleVoiceGateway.class).isActive();
			request.setAttribute("googleVoiceStatus", gvStatus);
		}
		if(gatewayManager.getGatewayByClass(SmsLibGateway.class)!=null){
			request.setAttribute("smsLibStatus", gatewayManager.getGatewayByClass(SmsLibGateway.class).isActive());
		}
		if(gatewayManager.getGatewayByClass(EmailGateway.class)!=null){
			request.setAttribute("emailStatus", gatewayManager.getGatewayByClass(EmailGateway.class).isActive());
		}
		if(gatewayManager.getGatewayByClass(OMailGateway.class)!=null){
			request.setAttribute("omailStatus", gatewayManager.getGatewayByClass(OMailGateway.class).isActive());
		}
		// add email config values
		if(gatewayManager.getGatewayByClass(EmailGateway.class) != null){
			request.setAttribute("emailMessageSubject", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_SUBJECT));
			request.setAttribute("emailMessageSignature", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_SIGNATURE));
			request.setAttribute("emailInProtocol", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_PROTOCOL));
			request.setAttribute("emailInHost", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_HOST));
			request.setAttribute("emailInPort", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_PORT));
			request.setAttribute("emailInAuth", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_AUTH));
			request.setAttribute("emailInTLS", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_TLS));
			request.setAttribute("emailInUsername", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_USERNAME));
			request.setAttribute("emailOutUseDefault", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_USEDEFAULT));
			request.setAttribute("emailOutProtocol", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_PROTOCOL));
			request.setAttribute("emailOutHost", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_HOST));
			request.setAttribute("emailOutPort", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_PORT));
			request.setAttribute("emailOutAuth", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_AUTH));
			request.setAttribute("emailOutTLS", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_TLS));
			request.setAttribute("emailOutFrom", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_FROM));
			request.setAttribute("emailOutUsername", adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_USERNAME));
		}
	}
	
	@RequestMapping(value="/module/messaging/changeGoogleVoiceCreds", method=RequestMethod.POST)
	public ModelAndView changeGoogleVoiceCredentials(
			@RequestParam("googleVoiceUsername") String username,
			@RequestParam("googleVoicePassword") String password1,
			@RequestParam("googleVoicePassword2") String password2,
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
				//tell the user it worked
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Credentials saved");
			} catch (Exception e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The credentials provided were not a valid Google Voice login");
			}
		}
		if (returnUrl == null)
			returnUrl = "manage_gateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
	
	@RequestMapping("/module/messaging/detectModems")
	public String addAddress(@RequestParam(value = "returnUrl", required = false) String returnUrl) {
		
		gatewayManager.getGatewayByClass(SmsLibGateway.class).startup();
		
		if (returnUrl == null)
			returnUrl = "manage_gateways.form";

		return "redirect:" + returnUrl;
	}

	@RequestMapping(value="/module/messaging/changeEmailCreds", method=RequestMethod.POST)
	public ModelAndView changeEmailCredentials(
			@RequestParam("messageSubject") String messageSubject,
			@RequestParam("messageSignature") String messageSignature,
			@RequestParam("inprotocol") String inprotocol,
			@RequestParam("inhost") String inhost,
			@RequestParam("inport") Integer inport,
			@RequestParam("inusername") String inusername,
			@RequestParam("inpwd1") String inpwd1,
			@RequestParam("inpwd2") String inpwd2,
			@RequestParam("inpwdchanged") Boolean inpwdchanged,
			@RequestParam("outprotocol") String outprotocol,
			@RequestParam("outhost") String outhost,
			@RequestParam("outport") Integer outport,
			@RequestParam("outfrom") String outfrom,
			@RequestParam("outusername") String outusername,
			@RequestParam("outpwd1") String outpwd1,
			@RequestParam("outpwd2") String outpwd2,
			@RequestParam("outpwdchanged") Boolean outpwdchanged,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request){
		
		HttpSession httpSession = request.getSession();
		AdministrationService adminService = Context.getAdministrationService();
		EncryptionService encryptionService = (EncryptionService) Context.getService(EncryptionService.class); 

		if(!OpenmrsUtil.nullSafeEquals(inpwd1,inpwd2))
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The incoming email passwords do not match");
		else if(!OpenmrsUtil.nullSafeEquals(outpwd1,outpwd2))
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The outgoing email passwords do not match");
		else if(inport == null)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The incoming email port is required");
		else if(outport == null)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The outgoing email port is required");
		else {
			try {
				// get all booleans (checkboxes) from the request
				Boolean inauth = request.getParameter("inauth") != null;
				Boolean intls = request.getParameter("intls") != null;
				Boolean usedefaultout = request.getParameter("usedefaultout") != null;
				Boolean outauth = request.getParameter("outauth") != null;
				Boolean outtls = request.getParameter("outtls") != null;

				// get the gateway
				EmailGateway gateway = gatewayManager.getGatewayByClass(EmailGateway.class);
				
				// update subject and signature
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_SUBJECT, messageSubject));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_SIGNATURE, messageSignature));
				
				// update incoming email server global properties
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_PROTOCOL, inprotocol));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_HOST, inhost));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_PORT, inport.toString()));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_AUTH, inauth.toString()));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_TLS, intls.toString()));
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_IN_USERNAME, inusername));
				if (inpwdchanged)
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_IN_PASSWORD, encryptionService.encrypt(inpwd1)));

				// update outgoing email server global properties
				adminService.saveGlobalProperty(
						new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_USEDEFAULT, usedefaultout.toString()));
				if (!usedefaultout) {
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_PROTOCOL, outprotocol));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_HOST, outhost));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_PORT, outport.toString()));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_AUTH, outauth.toString()));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_TLS, outtls.toString()));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_FROM, outfrom));
					adminService.saveGlobalProperty(
							new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_USERNAME, outusername));
					if (outpwdchanged)
						adminService.saveGlobalProperty(
								new GlobalProperty(MessagingConstants.GP_EMAIL_OUT_PASSWORD, encryptionService.encrypt(outpwd1)));
				}

				// restart the gateway if it is running
				if (gateway.isActive()) {
					gateway.shutdown();
					gateway.startup();
				}
				
				// kindly respond
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Credentials saved");
				
			} catch (Exception e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "There was an error while processing the email credentials");
			}
		}
		if (returnUrl == null)
			returnUrl = "manage_gateways.form";
		
		return new ModelAndView(new RedirectView(returnUrl));
	}

}
