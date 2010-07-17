package org.openmrs.module.messaging.googlevoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.sms.SmsProtocol;

import com.techventus.server.voice.Voice;

public class GoogleVoiceGateway extends MessagingGateway {

	private Voice googleVoice;
	private static Log log = LogFactory.getLog(GoogleVoiceGateway.class);
	
	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canSend() {
		return true;
	}

	@Override
	public List<MessagingAddress> getFromAddresses() {
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME);
		String password= Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD);
		Context.closeSession();
		List<MessagingAddress> addresses = new ArrayList<MessagingAddress>();
		addresses.add(new MessagingAddress(username, password,null));
		return addresses;
	}

	@Override
	public String getDescription() {
		return "Enables sending SMS via Google Voice";
	}

	@Override
	public String getName() {
		return "Google Voice";
	}

	@Override
	public boolean isActive() {
		return googleVoice != null && googleVoice.isLoggedIn();
	}

	@Override
	public void sendMessage(Message message) {
		try {
			googleVoice.sendSMS(message.getDestination(),message.getContent());
		} catch (IOException e) {
			log.error("Error sending SMS via Google Voice Gateway", e);
		}
	}

	@Override
	public boolean shouldSendMessage(Message m) {
		return false;
	}

	@Override
	public void shutdown() {}

	@Override
	public void startup() {
		MessagingAddress address = getFromAddresses().get(0);
		try {
			googleVoice = new Voice(address.getAddress(),address.getPassword());
			googleVoice.PRINT_TO_CONSOLE = false;
		} catch (IOException e) {
			log.error("Error starting the Google Voice Gateway",e);
		}
	}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getProtocolId() == SmsProtocol.PROTOCOL_ID;
	}

}
