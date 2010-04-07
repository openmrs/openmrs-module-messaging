package org.openmrs.module.messaging.sms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.sms.util.AllModemsDetector;
import org.smslib.AGateway;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;
import org.smslib.modem.SerialModemGateway;

public class SMSMessagingService extends MessagingService<SMSMessage, PhoneNumber> {

	protected boolean isStarted = false;
	
 	protected Service service;
 	
	protected PhoneNumberFactory phoneNumberFactory;
	
	protected SMSMessageFactory smsMessageFactory;
	
	public void setPhoneNumberFactory(PhoneNumberFactory factory){
		this.phoneNumberFactory = factory;
	}
	
	public void setSmsMessageFactory(SMSMessageFactory smsMessageFactory){
		this.smsMessageFactory = smsMessageFactory;
	}
	
	@Override
	public PhoneNumberFactory getAddressFactory() {
		return phoneNumberFactory;
	}
	
	@Override
	public SMSMessageFactory getMessageFactory() {
		return smsMessageFactory;
	}
	
	public Collection<SerialModemGateway> getActiveModems(){
		Collection<SerialModemGateway> results = new ArrayList<SerialModemGateway>();
		if(isStarted){
			for(AGateway gateway: service.getGateways()){
				if(gateway instanceof SerialModemGateway){
					results.add((SerialModemGateway) gateway);
				}
			}
		}
		return results;
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
		return null;
	}
	
	@Override
	public void sendMessage(String address, String content) {
		try {
			service.sendMessage(new OutboundMessage(address,content));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(SMSMessage message) {
		try {
			service.sendMessage(new OutboundMessage(message.getOrigin(),message.getContent()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(SMSMessage message, MessageDelegate delegate) {
		
	}
	
	@Override
	public void sendMessageToAddresses(SMSMessage m, List<String> addresses, MessageDelegate delegate) {
		
	}
	
	@Override
	public void sendMessages(List<SMSMessage> messages, MessageDelegate delegate) {
		
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
	}

	@Override
	public String getDescription() {
		return "A service that allows users to " +
				"send and recieve SMS messages " +
				"through an attached GSM phone or modem";
	}
	@Override
	public String getName() {
		return "SMS";
	}
	
}
