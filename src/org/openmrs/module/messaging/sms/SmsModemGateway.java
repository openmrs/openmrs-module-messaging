package org.openmrs.module.messaging.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.sms.util.AllModemsDetector;
import org.openmrs.module.messaging.sms.util.DetectorUtils;
import org.smslib.AGateway;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;
import org.smslib.modem.SerialModemGateway;

public class SmsModemGateway extends MessagingGateway<SmsMessage, PhoneNumber> {

	public SmsModemGateway(){}
	
	private final Log log = LogFactory.getLog(getClass());
	
	protected boolean isStarted = false;
	
 	protected Service service;
 	
 	protected List<ModemInfo> modems;
 	 	
	protected static PhoneNumberFactory phoneNumberFactory = new PhoneNumberFactory();
	
	protected static SmsMessageFactory smsMessageFactory = new SmsMessageFactory();
	
	@Override
	public PhoneNumberFactory getAddressFactory() {
		return phoneNumberFactory;
	}
	
	@Override
	public SmsMessageFactory getMessageFactory() {
		return smsMessageFactory;
	}
	
	public List<ModemInfo> getActiveModems(){
		return modems;
	}
	
	@Override
	public boolean canReceive() {
		return isStarted;
	}
	
	@Override
	public boolean canSend() {
		return isStarted;
	}
	
	@Override
	public PhoneNumber getDefaultSenderAddress() {
		return new PhoneNumber("+13173635376",null);
	}
	
	@Override
	public void sendMessage(String address, String content) throws Exception{
		service.sendMessage(new OutboundMessage(address,content));
	}
	
	@Override
	public void sendMessage(SmsMessage message) throws Exception{
		//set the origin of the message
		message.setOrigin(getDefaultSenderAddress().getAddress());
		//set the recipient
		Person p = Context.getService(MessagingAddressService.class).getPersonForAddress(message.getDestination());
		if(message.getRecipient() == null && p != null){
			message.setRecipient(p);
		}
		//set the sender
		if(message.getSender() == null && Context.getAuthenticatedUser()!=null){
			message.setSender(Context.getAuthenticatedUser().getPerson());
		}
		message.setDateSent(new Date());
		message.setDateReceived(new Date());
		message.setGatewayId(getGatewayId());
		service.sendMessage(new OutboundMessage(message.getDestination(),message.getContent()));
		Context.getService(MessageService.class).saveMessage(message);
	}
	
	@Override
	public void sendMessage(SmsMessage message, MessageDelegate delegate) {
		
	}
	
	@Override
	public void sendMessageToAddresses(SmsMessage m, List<String> addresses, MessageDelegate delegate) {
		
	}
	
	@Override
	public void sendMessages(List<SmsMessage> messages, MessageDelegate delegate) {
		
	}
	
	@Override
	public void shutdown() {
		try {
			service.stopService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void startup() {
		service = AllModemsDetector.getService();
		if(service.getServiceStatus() == ServiceStatus.STARTED && service.getGateways().size() > 0){
			isStarted=true;
		}
		modems = new ArrayList<ModemInfo>();
		if(isStarted){
			for(AGateway gateway: service.getGateways()){
				if(gateway instanceof SerialModemGateway){
					SerialModemGateway smg = (SerialModemGateway) gateway;
					modems.add(DetectorUtils.getInfoForGateway(smg));
				}
			}
		}
	}
	
	public void detectModems(){
		startup();
	}

	@Override
	public String getDescription() {
		return "A service that allows users to " +
				"send and recieve Sms messages " +
				"through attached GSM phones or modems";
	}
	@Override
	public String getName() {
		return "Sms Modem";
	}

	@Override
	public String getGatewayId() {
		return "smsmodem";
	}

	@Override
	public boolean canSendFromUserAddresses() {
		return false;
	}
	
}
