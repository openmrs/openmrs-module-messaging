package org.openmrs.module.messaging.framework;

import java.util.ArrayList;

import org.openmrs.Person;

public class MessagingCenter {

	private ArrayList<MessagingService> services;
	
	public void initServices(){
	}
	
	public void sendMessageViaPreferredMethod(Person destination, Message m){
		
	}
	
	public void registerListenerForPerson(MessagingServiceListener listener, Person p){
		
	}
	
	public void registerListener(MessagingServiceListener listener){}
	
	/**
	 * Returns the messaging service singleton for the provided class
	 * @param messagingServiceClass
	 * @return
	 */
	public MessagingService getMessagingService(Class messagingServiceClass){
		for(MessagingService mService: services){
			if(mService.getClass().equals(messagingServiceClass)){
				return mService;
			}
		}
		return null;
	}
	
	public ArrayList<MessagingService> getAllMessagingServices(){
		return services;
	}
}
