package org.openmrs.module.messaging.sms;

import java.util.List;

import org.openmrs.module.messaging.schema.MessageAddress;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessagingService;
import org.openmrs.module.messaging.sms.serial.SerialModemFinder;
import org.smslib.modem.SerialModemGateway;

public class SMSMessagingService extends MessagingService<SMSMessage, PhoneNumber> {

	protected boolean isStarted = false;
	protected PhoneNumber defaultNumber;
	protected List<SerialModemGateway> modems;
	
	@Override
	public boolean canReceive() {
		return isStarted;
	}

	@Override
	public boolean canSend() {
		return isStarted;
	}

	@Override
	public MessageAddress getDefaultSenderAddress() {
		return null;
	}

	@Override
	public void sendMessage(SMSMessage message, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessageToAddresses(SMSMessage m,
			List<PhoneNumber> addresses, MessageDelegate delegate) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startup() {
		modems = SerialModemFinder.getModemGateways();
		if(modems.size() > 0){
			try {
				//defaultNumber = PhoneNumberFactory.createAddress(modems.get(0).sendCustomATCommand("AT+CNUM"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendMessages(List<SMSMessage> messages, MessageDelegate delegate) {
		
	}

	@Override
	public void sendMessage(SMSMessage message) {
		
	}

}
