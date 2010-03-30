package org.openmrs.module.messaging.schema;

import java.util.ArrayList;

import org.openmrs.Person;
import org.openmrs.module.messaging.sms.SMSMessagingService;

/**
 * The Messaging Center is the main singleton in the Messaging framework. It is
 * focused on cross-service functionality like sending to preferred messaging
 * addresses and listening across all messaging services.
 * 
 * @author Dieterich
 * 
 */
public class MessagingCenter {
	
	protected static MessagingCenter instance;
	
	public static MessagingCenter getInstance(){
		if(instance == null){
			instance = new MessagingCenter();
		}
		return instance;
	}
	
	public MessagingCenter(){
		services = new ArrayList<MessagingService>();
	}
	
	private static ArrayList<MessagingService> services;

	public void initServices() {
		SMSMessagingService smsService = new SMSMessagingService();
		services.add(smsService);
		
		for(MessagingService ms:services){
			ms.startup();
		}
	}

	public void sendMessageViaPreferredMethod(Person destination, Message m) {

	}

	public void registerListenerForPerson(MessagingServiceListener listener, Person p) {

	}

	public void registerListener(MessagingServiceListener listener) {
	}

	/**
	 * Returns the messaging service singleton for the provided class
	 * 
	 * @param messagingServiceClass
	 * @return
	 */
	public static MessagingService getMessagingService(Class messagingServiceClass) {
		for (MessagingService mService : services) {
			if (mService.getClass().equals(messagingServiceClass)) {
				return mService;
			}
		}
		return null;
	}
	
	public static MessagingService getMessagingServiceForName(String name) {
		for (MessagingService mService : services) {
			if (mService.getName().equalsIgnoreCase(name)) {
				return mService;
			}
		}
		return null;
	}
	
	public static AddressFactory getAddressFactoryForService(Class messagingServiceClass) {
		try{
			return getMessagingService(messagingServiceClass).getAddressFactory();
		}catch(Exception e){
			return null;
		}
	}
	
	public static ArrayList<MessageFactory> getMessageFactories(){
		ArrayList<MessageFactory> factories = new ArrayList<MessageFactory>();
		for(MessagingService service:services){
			factories.add(service.getMessageFactory());
		}
		return factories;
	}
	
	public static ArrayList<AddressFactory> getAddressFactories(){
		ArrayList<AddressFactory> factories = new ArrayList<AddressFactory>();
		for(MessagingService service:services){
			factories.add(service.getAddressFactory());
		}
		return factories;
	}
	
	public static MessageFactory getMessageFactoryForService(Class messagingServiceClass) {
		try{
			return getMessagingService(messagingServiceClass).getMessageFactory();
		}catch(Exception e){
			return null;
		}
	}

	public static ArrayList<MessagingService> getAllMessagingServices() {
		return services;
	}
}
