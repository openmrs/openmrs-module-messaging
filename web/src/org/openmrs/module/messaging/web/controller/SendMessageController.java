package org.openmrs.module.messaging.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.schema.exception.AddressFormattingException;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SendMessageController {

	protected static final Log log = LogFactory.getLog(SendMessageController.class);

	@InitBinder
	public void initBinder(WebDataBinder wdb) {
		wdb.registerCustomEditor(Person.class, new PersonEditor());
	}

	/**
	 * Sends a message (content) to the address specified in toAddress. If
	 * neither the toAddress nor the fromAddress are already in the OpenMRS
	 * system with an attached protocol, then a protocol must be supplied. All
	 * parameters besides content, and toAddress, are optional. It is
	 * possible to have a message that has a sender that does not own the
	 * 'fromAddress' - e.g. The user is sending from a default OpenMRS address
	 * like a mobile phone hooked up to the OpenMRS server. In this case the
	 * 'sender' and 'fromAddress' would not match up.
	 * 
	 * @param content
	 *            The content of the message
	 * @param toAddressString
	 *            The address to send the message to
	 * @param fromAddressString
	 *            The address that the message originates from
	 * @param sender
	 *            The sender of the message
	 * @param recipient
	 *            The recipient of the message
	 * @param protocolId
	 *            The protocol of the message
	 * @param fromCurrentUser
	 *            Is true if the message is from the current user - fills in the
	 *            "sender" field with the currently authenticated user
	 * @param returnUrl
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/module/messaging/admin/sendMessage", method=RequestMethod.POST)
	protected String sendMessage(
			@RequestParam("content") String content,
			@RequestParam("toAddress") String toAddressString,
			@RequestParam(value = "fromAddress", required = false) String fromAddressString,
			@RequestParam(value = "sender", required = false) Person sender,
			@RequestParam(value = "recipient", required = false) Person recipient,
			@RequestParam(value = "protocol", required = false) String protocolId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			HttpServletRequest request) {
		MessagingService messagingService = Context.getService(MessagingService.class);
		// get the http session
		HttpSession httpSession = request.getSession();
		//setup the returnURL
		if (returnUrl == null || returnUrl.equals("")){ 
			returnUrl = "/module/messaging/admin/sendMessage.form";
		}
		returnUrl = "redirect:" + returnUrl;
		MessagingAddressService addressService = Context.getService(MessagingAddressService.class);
		Message message = null;
		//first we see if the addresses are already in the system
		MessagingAddress toAddress = addressService.getMessagingAddress(toAddressString);
		MessagingAddress fromAddress = addressService.getMessagingAddress(fromAddressString);
		Protocol protocol = null;
		//attempt to pull the protocol from the messaging addresses
		if(toAddress != null) protocol = messagingService.getProtocolById(toAddress.getProtocolId());
		if(fromAddress != null) protocol = messagingService.getProtocolById(fromAddress.getProtocolId());
		//if pulling the protocol from the addresses didn't work, 
		// then the protocol should have been passed in as a string
		if(protocol == null) protocol = messagingService.getProtocolById(protocolId);
		//check to make sure that the supplied protocol exists...
		if(protocol == null){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Non-existent protocol specified");
			return returnUrl;
		}
		//if the toAddress didn't exist in the system, create it
		if(toAddress == null){
			try{
				toAddress = protocol.createAddress(toAddressString, null);
			}catch (AddressFormattingException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,"To-address \""+toAddressString+"\" was badly formatted.");
				return returnUrl;
			}
		}
		// if the fromAddress didn't exist in the system, create it
		if(fromAddress == null && fromAddressString!=null && !fromAddressString.equals("")){
			try{
				fromAddress = protocol.createAddress(fromAddressString, null);
			}catch (AddressFormattingException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,"From-address \""+toAddressString+"\" was badly formatted.");
				return returnUrl;
			}
		}
		//create the message itself
		try{
			message = protocol.createMessage(content, toAddressString,fromAddressString);
		}catch(Exception e){
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,e.getMessage());
			return returnUrl;
		}
		//set the people involved
		message.setSender(sender);
		message.setRecipient(recipient);
		if(message.getRecipient()== null){
			message.setRecipient(addressService.getPersonForAddress(toAddressString));
		}
		//if it is possible to send the message, do so
		if (!messagingService.canSendToProtocol(protocol)) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "There is not currently a gateway running that can send that type of message.");
			return returnUrl;
		} else {
			messagingService.sendMessage(message);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Message placed in outbox.");
			return returnUrl;
		}
	}

	@RequestMapping(value = "/module/messaging/admin/sendMessage")
	public void populateModel(HttpServletRequest request) {
		request.setAttribute("user",Context.getAuthenticatedUser().getPerson());
		List<MessagingAddress> fromAddresses = Context.getService(MessagingAddressService.class).getMessagingAddressesForPerson(Context.getAuthenticatedUser().getPerson());
		request.setAttribute("fromAddresses", fromAddresses);
	}
}
