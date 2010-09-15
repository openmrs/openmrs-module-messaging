package org.openmrs.module.messaging.googlevoice;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.CredentialSet;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.sms.SmsProtocol;

import com.techventus.server.voice.Voice;

public class GoogleVoiceGateway extends MessagingGateway {

	private Voice googleVoice;
	private static Log log = LogFactory.getLog(GoogleVoiceGateway.class);
	private CredentialSet currentCredentials;
	
	
	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canSend() {
		return true;
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
	public void sendMessage(Message message) throws Exception{
		googleVoice.sendSMS(message.getDestination(),message.getContent());
	}

	@Override
	public boolean shouldSendMessage(Message m) {
		return false;
	}

	@Override
	public void shutdown() {
		googleVoice = null;
	}


	
	@Override
	public void startup() {
		//update the stored credentials
		currentCredentials = getCredentials();
		try {
			googleVoice = new Voice(currentCredentials.getUsername(),currentCredentials.getPassword());
			//turn off GV logging
			googleVoice.PRINT_TO_CONSOLE = false;
		} catch (Exception e) {
			log.error("Error starting the Google Voice Gateway",e);
		}
	}

	/**
	 * Returns a pair of strings. The first string is the username
	 * and the second string is the password
	 * @return
	 */
	private CredentialSet getCredentials(){
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME);
		String password= Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD);
		Context.closeSession();
		CredentialSet credentials = new CredentialSet(username,password);
		return credentials;
	}
	
	public void updateCredentials(String username, String password){
		this.currentCredentials = new CredentialSet(username, password);
		try {
			this.googleVoice = new Voice(username,password);
		} catch (IOException e) {
			log.error("Error updating Google Voice login info",e);
		}
	}

	
	@Override
	public boolean supportsProtocol(Protocol p) {
		return p.getProtocolId() == SmsProtocol.PROTOCOL_ID;
	}

}
