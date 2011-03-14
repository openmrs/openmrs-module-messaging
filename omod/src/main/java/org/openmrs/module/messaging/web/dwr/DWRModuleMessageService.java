package org.openmrs.module.messaging.web.dwr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.domain.gateway.exception.AddressFormattingException;
import org.openmrs.module.messaging.web.domain.MessageBean;

public class DWRModuleMessageService {
	
	private static Log log = LogFactory.getLog(DWRModuleMessageService.class);
	
	private MessageService messageService;
	private MessagingService messagingService;
	private MessagingAddressService addressService;
	
	/**
	* The max amount of time (in milliseconds) that 2 messages
	* can be apart before they are grouped separately.
	* Currently it is 1 hour.
	*/
	private static final long MAX_TIME_DISTANCE = 3600000;
	
	public DWRModuleMessageService(){
		messageService = Context.getService(MessageService.class);
		messagingService = Context.getService(MessagingService.class);
		addressService = Context.getService(MessagingAddressService.class);
	}
	
	/**
	 * Returns all messages to or from a patient in the form of a list of {@link MessageBean}s.
	 * @param patientId
	 * @return
	 */
	public List<MessageBean> getMessagesForPatient(Integer patientId){
//		//retreive the patient
//		Patient p = Context.getService(PatientService.class).getPatient(patientId);
//		List<MessageBean> results = new ArrayList<MessageBean>();
//		List<Message> messages = messageService.getMessagesToOrFromPerson(p);
//		MessageBean messageBean = null;
//		Map<String,Integer> colorNumbers = new HashMap<String,Integer>();
//		//holds the last used color number
//		int colorNumber = 0;
//		//holds the time (in milliseconds) of the last message
//		long lastTime = 0;
//		//holds the last used time row Id 
//		int timeId = -1;
//		for(Message message: messages){
//			//create the new message bean
//			messageBean = new MessageBean(message);
//			messageBean.setFromOpenMRS(message.getTo() != null && message.getTo().size() > 0 && message.getToPeople().contains(p));
//			//set the proper 'color number'
//			if(messageBean.isFromOpenMRS() && !colorNumbers.containsKey(message.getOrigin())){
//				colorNumbers.put(message.getOrigin(), colorNumber);
//				messageBean.setColorNumber(colorNumber++);
//			}else if(messageBean.isFromOpenMRS()){
//				messageBean.setColorNumber(colorNumbers.get(message.getOrigin()));
//			}
//			//if this message was sent too far apart from the last one,
//			//insert a date marker (empty MessageBean w/Date)
//			if(isTooFarApart(lastTime,message.getDate())){
//				MessageBean mb = new MessageBean();
//				mb.setDateAndTime(message.getDate());
//				mb.setId(timeId--);
//				results.add(mb);
//				lastTime = message.getDate().getTime();
//			}
//			results.add(messageBean);
//		}
		return null;
	}
	
	private boolean isTooFarApart(long lastTime, Date thisTime){
		return Math.abs(thisTime.getTime() - lastTime) > MAX_TIME_DISTANCE;
	}
	
	
	public String sendMessage(String content, String toAddress, String protocolId){
		return sendMessage(content,toAddress,null,null,null,protocolId,false);
	}
	
	public String sendMessage(String content, String toAddressString, String fromAddressString, Integer recipientId, Integer senderId, String protocolId, boolean isFromCurrentUser){
//		Message message = null;
//		//first we see if the addresses are already in the system
//		MessagingAddress toAddress = addressService.getMessagingAddress(toAddressString);
//		MessagingAddress fromAddress = addressService.getMessagingAddress(fromAddressString);
//		Protocol protocol = null;
//		//attempt to pull the protocol from the messaging addresses
//		if(toAddress != null) protocol = messagingService.getProtocolByClass(toAddress.getProtocol());
//		if(fromAddress != null) protocol = messagingService.getProtocolByClass(fromAddress.getProtocol());
//		//if pulling the protocol from the addresses didn't work, 
//		// then the protocol should have been passed in as a string
//		if(protocol == null) protocol = messagingService.getProtocolByClass((Class<? extends Protocol>) Class.forName(protocolId));
//		//check to make sure that the supplied protocol exists...
//		if(protocol == null){
//			return "Non-existent protocol specified";
//		}
//		//if the toAddress didn't exist in the system, create it
//		if(toAddress == null){
//			try{
//				toAddress = protocol.createAddress(toAddressString, null);
//			}catch (AddressFormattingException e) {
//				return "To-address \""+toAddressString+"\" was badly formatted.";
//			}
//		}
//		// if the fromAddress didn't exist in the system, create it
//		// here we have slightly more complicated conditions (as opposed to the toAddress)
//		// because it's ok to not have a from address
//		if(fromAddress == null && fromAddressString!=null && !fromAddressString.equals("")){
//			try{
//				fromAddress = protocol.createAddress(fromAddressString, null);
//			}catch (AddressFormattingException e) {
//				return "From-address \""+toAddressString+"\" was badly formatted.";
//			}
//		}
//		//create the message itself
//		try{
//			message = protocol.createMessage(content, toAddressString,fromAddressString);
//		}catch(Exception e){
//			return e.getMessage();
//		}
//		//set the people involved
//		PersonService ps = Context.getPersonService();
//		message.setSender(ps.getPerson(senderId));
//		//message.setRecipient(ps.getPerson(recipientId));
//		message.setDate(new Date());
//		//if people were not passed in, try to pull the people from the addresses
//		if(message.getRecipient() == null){
//			message.setRecipient(addressService.getPersonForAddress(toAddressString));
//		}
//		if(isFromCurrentUser){
//			message.setFrom(Context.getAuthenticatedUser().getPerson());
//		}
//		//if it is possible to send the message, do so
//		if (!messagingService.canSendToProtocol(protocol)) {
//			return "There is not currently a gateway running that can send that type of message.";
//		} else {
//			messagingService.sendMessage(message);
//			return null;
//		}
		return "Cannot currently send messages";
	}
	
	public List<MessageBean> getMessagesForAuthenticatedUser(Integer pageNumber, boolean to){
		return getMessagesForAuthenticatedUserWithPageSize(pageNumber, 10, to);
	}
	
	public List<MessageBean> getMessagesForAuthenticatedUserWithPageSize(Integer pageNumber, Integer pageSize, boolean to){
		return getMessagesForPerson(pageNumber,pageSize, Context.getAuthenticatedUser().getId(),to);
	} 
	
	public List<MessageBean> getMessagesForPerson(Integer pageNumber, Integer pageSize, Integer personId, boolean to){
		List<MessageBean> beans = new ArrayList<MessageBean>();
		List<Message> messages = messageService.getMessagesForPersonPaged(pageNumber, pageSize, personId, to);
		for(Message m: messages){
			beans.add(new MessageBean(m));
		}
		return beans;
	}
}