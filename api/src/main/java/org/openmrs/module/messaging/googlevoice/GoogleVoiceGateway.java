package org.openmrs.module.messaging.googlevoice;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.sms.SmsProtocol;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.module.messaging.util.Pair;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.datatypes.Phone;

public class GoogleVoiceGateway extends MessagingGateway {

	/**
	 * The object that allows control of a google voice account
	 */
	private Voice googleVoice;
	
	private Pair<String,String> credentials;
	
	/**
	 * Boolean indicating whether or not this gateway is connected to Twitter
	 */
	private volatile boolean isActive = false;
	
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
	public String getDescription() {
		return "Enables sending SMS via Google Voice";
	}

	@Override
	public String getName() {
		return "Google Voice";
	}

	@Override
	public boolean isActive() {
		updateCredentials();
		return isActive;
	}

	@Override
	public void sendMessage(Message message,MessageRecipient recipient) throws Exception{
		updateCredentials();
		googleVoice.sendSMS(recipient.getRecipient().getAddress(), message.getContent());
		Phone phone = googleVoice.getSettings(false).getPhones()[0];
		if(phone != null) recipient.setOrigin(phone.getFormattedNumber());
	}

	@Override
	public void shutdown() {
		googleVoice = null;
		isActive=false;
	}
	
	@Override
	public void startup() {
		updateCredentials();
		if(googleVoice != null){
			googleVoice.PRINT_TO_CONSOLE = false;
		}
		isActive=true;
	}
	
	/**
	 * Returns a pair of strings. The first string is the username
	 * and the second string is the password
	 * @return
	 */
	private Pair<String, String> getCredentials(){
		String username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_UNAME);
		String password= Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_GOOGLE_VOICE_PWORD);
		Pair<String,String> creds= new Pair<String,String>(username,password);
		return creds;
	}
	
	public void updateCredentials(){
		if(credentials == null || !credentials.equals(getCredentials())){
			this.credentials = getCredentials();
		}
		try {
			this.googleVoice = new Voice(credentials.first,credentials.second);
		} catch (IOException e) {
			isActive = false;
			log.error("Error updating Google Voice login info",e);
		}
	}
	
	@Override
	public boolean supportsProtocol(Class<? extends Protocol> p) {
		return p.equals(SmsProtocol.class);
	}

	@Override
	public void receiveMessages() {
		//do nothing
	}

}
