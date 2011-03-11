package org.openmrs.module.messaging.googlevoice;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.messaging.domain.gateway.CredentialSet;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.sms.SmsProtocol;
import org.openmrs.module.messaging.util.MessagingConstants;

import com.techventus.server.voice.Voice;

public class GoogleVoiceGateway extends MessagingGateway {

	/**
	 * The object that allows control of a google voice account
	 */
	private Voice googleVoice;
	
	/**
	 * The credentials of the currently logged in user
	 */
	private CredentialSet currentCredentials;

	/**
	 * Boolean indicating whether or not this gateway is connected to Twitter
	 */
	private volatile boolean isActive = false;
	
	/**
	 * Boolean used for starting and stopping the activity-checking thread
	 */
	private volatile boolean stopThread = false;

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
		return isActive;
	}

	private void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public void sendMessage(Message message) throws Exception{
		try{
			for(MessagingAddress address: message.getTo()){
				googleVoice.sendSMS(address.getAddress(),message.getContent());
			}
			message.setStatus(MessageStatus.SENT);
		}catch(Throwable t){
			log.error("Google Voice Gateway failed to send message",t);
			message.setStatus(MessageStatus.FAILED);
		}
		getMessageService().saveMessage(message);
	}

	@Override
	public void shutdown() {
		googleVoice = null;
		stopThread();
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
		startActivityCheckingThread();
	}
	
	/**
	 * Starts a thread that polls twitter every 2 seconds to see if the gateway
	 * is still connected.
	 */
	private void startActivityCheckingThread() {
		stopThread = false;
		Thread activeCheckingThread = new Thread(new Runnable() {
			public void run() {
				while (!stopThread) {
					if (googleVoice != null) {
						try {
							if (googleVoice.isLoggedIn()) {
								setActive(true);
								System.out.println("Google voice was active");
							}else{
								setActive(false);
								System.out.println("Google voice was inactive");
							}
						} catch (Throwable t) {
							setActive(false);
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							log.error("Error sleeping in Google Voice activity checking thread",e);
						}
					}
				}
				System.out.println("Stopping the thread");
			}
		});
		activeCheckingThread.start();
	}

	private void stopThread() {
		stopThread = true;
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
		return p.getClass() == SmsProtocol.class;
	}

	@Override
	public void receiveMessages() {
		//do nothing
	}

}
