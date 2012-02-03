package org.openmrs.module.messaging.web.dwr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.omail.OMailProtocol;
import org.openmrs.module.messaging.web.domain.MessageBean;
import org.openmrs.module.messaging.web.domain.MessageBeanSet;

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
	
	public String sendMessage(String content, String toAddressString, String subject, boolean isFromCurrentUser){
		Set<MessagingAddress> toAddresses = new HashSet<MessagingAddress>();
		//first we split apart the addresses
		String[] addresses = toAddressString.split(",");
		for(String s: addresses){
			String adrString = null;
			try{
				adrString = s.substring(s.indexOf("<")+1, s.indexOf(">"));
			}catch(Throwable t){
				continue;
			}
			//now we check to make sure that we're sending to valid, pre-existing addresses
			Class<? extends Protocol> protocolClass = messagingService.getProtocolByAbbreviation(adrString.split(":")[0]).getClass();
			List<MessagingAddress> existingAddresses = addressService.findMessagingAddresses(adrString.split(":")[1], protocolClass, null,false);
			if(existingAddresses.size() != 1){
				return "Could not find address: "+ s;
			}else{
				toAddresses.add(existingAddresses.get(0));
			}
		}
		//check that we're actually sending to an address
		if(toAddresses.size() < 1){
			return "No addresses entered";
		}
		//figure out the sender
		Person sender = null;
		if(isFromCurrentUser) sender = Context.getAuthenticatedUser().getPerson();
		
		//create the message
		Message message = new Message(toAddresses,sender,content);
		message.setSubject(subject!=null?subject:"");
		
		//if it is possible to send the message, do so
		try{	
			messagingService.sendMessage(message);
			return null;
		}catch(Throwable t){
			return t.getMessage();
		}
	}
	
	public MessageBeanSet getMessagesForAuthenticatedUser(Integer pageNumber, boolean to){
		return getMessagesForAuthenticatedUserWithPageSize(pageNumber, 10, to);
	}
	
	public MessageBeanSet getMessagesForAuthenticatedUserWithPageSize(Integer pageNumber, Integer pageSize, boolean to){
		return getMessagesForPerson(pageNumber,pageSize, Context.getAuthenticatedUser().getPerson().getId(),to);
	} 
	
	public MessageBeanSet getMessagesForPerson(Integer pageNumber, Integer pageSize, Integer personId, boolean to){
		List<MessageBean> beans = new ArrayList<MessageBean>();
		List<Message> messages = new ArrayList<Message>();
		Integer total=null;
		if(to){
			messages = messageService.getMessagesForPersonPaged(pageNumber, pageSize, personId, to,false,OMailProtocol.class);
			total = messageService.countMessagesForPerson(personId, to,OMailProtocol.class);
		}else{
			messages = messageService.getMessagesForPersonPaged(pageNumber, pageSize, personId, to,false,null);
			total = messageService.countMessagesForPerson(personId, to,null);
		}
		for(Message m: messages){
			beans.add(new MessageBean(m));
		}
		MessageBeanSet resultSet = new MessageBeanSet(beans,total,pageNumber);
		return resultSet;
	}
	
	public MessageBeanSet searchMessages(Integer pageNumber, Integer pageSize, Integer personId, String searchString, boolean inbox, boolean outbox){
		List<MessageBean> beans = new ArrayList<MessageBean>();
		Person p = Context.getPersonService().getPerson(personId);
		List<Message> messages = messageService.searchMessages(pageNumber, pageSize, searchString, p, inbox,outbox,false);
		for(Message m: messages){
			beans.add(new MessageBean(m));
		}
		Integer total = messageService.countSearch(p, searchString,inbox,outbox);
		MessageBeanSet resultSet = new MessageBeanSet(beans,total,pageNumber);
		return resultSet;			
	}
	
	public MessageBeanSet searchMessagesForAuthenticatedUser(Integer pageNumber, Integer pageSize, String searchString, boolean inbox, boolean outbox){
		Integer personId = Context.getAuthenticatedUser().getPerson().getId();
		return searchMessages(pageNumber, pageSize, personId, searchString, inbox,outbox);
	}
	
	public void markMessageAsReadForAuthenticatedUser(int messageId){
		Message m = Context.getService(MessageService.class).getMessage(messageId);
		Person p = Context.getAuthenticatedUser().getPerson();
		for(MessageRecipient mr: m.getTo()){
			if(mr.getRecipient().getPerson().equals(p)){
				mr.setRead(true);
				break;
			}
		}
		Context.getService(MessageService.class).saveMessage(m);
	}
}