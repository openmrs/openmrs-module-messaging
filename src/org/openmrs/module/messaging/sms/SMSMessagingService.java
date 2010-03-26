package org.openmrs.module.messaging.sms;

import java.util.List;

import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;
import org.smslib.modem.SerialModemGateway;

public class SMSMessagingService extends MessagingService<SMSMessage, PhoneNumber> {

	protected boolean isStarted = false;
	protected PhoneNumber defaultNumber;
	protected List<SerialModemGateway> modems;
	
	@Override
	public boolean canReceive() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean canSend() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public MessagingAddress getDefaultSenderAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void sendMessage(String address, String content) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendMessage(SMSMessage message) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendMessage(SMSMessage message, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendMessageToAddresses(SMSMessage m, List<String> addresses,
			MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendMessages(List<SMSMessage> messages, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}
	
	
}
