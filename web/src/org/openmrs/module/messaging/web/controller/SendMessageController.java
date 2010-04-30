package org.openmrs.module.messaging.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageFormattingException;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
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
public class SendMessageController{

	protected static final Log log = LogFactory.getLog(SendMessageController.class);
	
	@InitBinder
	public void initBinder(WebDataBinder wdb) {
		wdb.registerCustomEditor(Person.class, new PersonEditor());
	}
	
	/**
	 * Sends a message to the specified address
	 * fromAddress, sender, recipient, and gateway are all optional
	 * parameters. However, if an address is specified that more than one
	 * gateway can send to, and a gateway is not specified, then an error is thrown
	 * @param toAddressString
	 * @param fromAddressString
	 * @param sender
	 * @param recipient
	 * @param gatewayName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/module/messaging/sendMessage", method = RequestMethod.POST)
	protected String sendMessage(
			@RequestParam("content") String content,
			@RequestParam("toAddress") String toAddressString,
			@RequestParam(value="fromAddress",required=false) String fromAddressString,
			@RequestParam(value="fromCurrentUser",required=false) Boolean fromCurrentUser,
			@RequestParam(value="sender",required=false) Person sender,
			@RequestParam(value="recipient",required=false) Person recipient,
			@RequestParam(value="gateway",required=false) String gatewayName,
			@RequestParam(value="returnUrl", required=false) String returnUrl,
			HttpServletRequest request){
		
		//get the http session
		HttpSession httpSession = request.getSession();
		
		if (returnUrl == null || returnUrl.equals(""))
			returnUrl = "sendMessage.form";

		returnUrl= "redirect:" + returnUrl;
		
		MessagingGateway mg = null;
		//if the service parameter is specified, then use that
		if(gatewayName != null){
			mg = MessagingService.getInstance().getMessagingGatewayForName(gatewayName);			
		}else{
			//otherwise, attempt to infer it from the supplied address
			List<MessagingGateway> gateways = MessagingService.getInstance().getMessagingGatewaysForAddress(toAddressString);
			if(gateways.size() == 1){
				mg = gateways.get(0);
			}else{
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "No service specified to send the message");
				return returnUrl;
			}
		}
		
		MessagingAddress toAddress = null;
		MessagingAddress fromAddress = null;
		Message message = null;
		
		//check the validity of the address
		try{
			toAddress = mg.getAddressFactory().createAddress(toAddressString,null);
			if(fromAddressString!=null && !fromAddressString.equals("")){
				fromAddress = mg.getAddressFactory().createAddress(fromAddressString,null);
			}else if(fromCurrentUser){
					List<MessagingAddress> fromAddresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPersonAndGateway(Context.getAuthenticatedUser().getPerson(), mg);
					if(fromAddresses.size() == 1){
						fromAddress = fromAddresses.get(0);
					}else{
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Current user has more than one address for that gateway. Please specify an address");
						return returnUrl;
					}
			}
			message = mg.getMessageFactory().createMessage(content, fromAddress, toAddress);
			message.setSender(sender);
			message.setRecipient(recipient);
		}catch(MessageFormattingException e){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getDescription());
			return returnUrl;
		}catch(AddressFormattingException e){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getDescription());
			return returnUrl;
		}
		
		if(!mg.canSend()){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "The "+mg.getName()+" service is not currently running");
			return returnUrl;
		}else{
			try{
				mg.sendMessage(message);
			}catch(Exception e){
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Message could not be sent : " + e.getMessage());
				log.error("Could not send a message",e);
				return returnUrl;
			}
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Message sent successfully!");
			return returnUrl;
		}
	
	}
	@RequestMapping(value="/module/messaging/admin/sendaMessage")
    public void populateModel(HttpServletRequest request){
    	ArrayList<String> services = new ArrayList<String>();
    	for(MessagingGateway mg: MessagingService.getInstance().getAllMessagingGateways()){
    		if(mg.canSend()){
    			services.add(mg.getName());
    		}
    	}
    	request.setAttribute("services", services);
    }
}
