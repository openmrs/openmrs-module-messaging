package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.sms.PhoneNumber;
import org.openmrs.module.messaging.sms.SmsModemGateway;
import org.openmrs.module.messaging.twitter.TwitterAddress;
import org.openmrs.module.messaging.twitter.TwitterGateway;

/**
 * The Messaging Center is the main singleton in the Messaging framework. It is
 * focused on cross-service functionality like sending to preferred messaging
 * addresses and listening across all messaging gateways.
 * 
 * @author Dieterich
 * 
 */
public class MessagingService {
	
	protected static Log log = LogFactory.getLog(MessagingService.class);
	
	protected static MessagingService instance;
	
	protected static HashMap<String,Class> addressTypes;
	
	static{
		addressTypes = new HashMap<String, Class>();
		addressTypes.put("Phone Number", PhoneNumber.class);
		addressTypes.put("Twitter Username", TwitterAddress.class);
	}
	
	protected CopyOnWriteArraySet<MessagingGateway> gateways;
	
	//for Hibernate only
	public void setGateways(Set gateways){
		this.gateways = new CopyOnWriteArraySet<MessagingGateway>(gateways);
		log.info("Services initialized: ");
		for(MessagingGateway ms: this.gateways){
			log.info("service: " + ms.getName());
		}
		
	}
	
	public void initGateways() {
		for(MessagingGateway ms:gateways){
				ms.startup();
		}
	}
	
	public MessagingService(){}
	
	public static MessagingService getInstance(){
		if(instance != null){
			return instance;
		}else{
			instance = new MessagingService();
			HashSet<MessagingGateway> s = new HashSet<MessagingGateway>();
			s.add(new SmsModemGateway());
			s.add(new TwitterGateway());
			instance.setGateways(s);
			return instance;
		}
	}



	public void sendMessageViaPreferredMethod(Person destination, Message m) {}

	public void registerListenerForPerson(MessagingServiceListener listener, Person p) {}

	public void registerListener(MessagingServiceListener listener) {}

	//> service getter methods
	
	/**
	 * Returns the messaging service singleton for the provided class
	 * 
	 * @param messagingGatewayClass
	 * @return
	 */
	public <S extends MessagingGateway> S getMessagingGateway(Class<? extends S> messagingGatewayClass) {
		for (MessagingGateway mService : gateways) {
			if (mService.getClass().equals(messagingGatewayClass)) {
				return (S) mService;
			}
		}
		return null;
	}
	
	/**
	 * @param name
	 * @return a messaging gateway that has a name that matches the parameter
	 */
	public MessagingGateway getMessagingGatewayForName(String name) {
		for (MessagingGateway mService : gateways) {
			if (mService.getName().equalsIgnoreCase(name)) {
				return mService;
			}
		}
		return null;
	}
	
	/**
	 * @param id
	 * @return a messaging gateway that has an id that matches the parameter
	 */
	public MessagingGateway getMessagingGatewayForId(String id) {
		for (MessagingGateway mService : gateways) {
			if (mService.getGatewayId().equalsIgnoreCase(id)) {
				return mService;
			}
		}
		return null;
	}
	
	/**
	 * @return All messaging gateways
	 */
	public Set<MessagingGateway> getAllMessagingGateways() {
		return gateways;
	}
	
	
	/**
	 * @param m
	 * @return A list of all messaging gateways that can send that type of message
	 */
	public List<MessagingGateway> getMessagingGatewaysForMessage(Message m){
		List<MessagingGateway> gateways= new ArrayList<MessagingGateway>();
		//search through all the messaging gateways
		for(MessagingGateway ms: getAllMessagingGateways()){
			//if the gateway deals with the proper class
			if(ms.getMessageClass().equals(m.getClass())){
					gateways.add(ms);
			}
		}
		return gateways;
	}
	
	/**
	 * @param a
	 * @return A lit of all messaging gateways that can send to that type of address
	 */
	public List<MessagingGateway> getMessagingGatewaysForAddress(MessagingAddress a){
		List<MessagingGateway> gateways= new ArrayList<MessagingGateway>();
		for(MessagingGateway ms: getAllMessagingGateways()){
			if(ms.getMessagingAddressClass().equals(a.getClass())){
					gateways.add(ms);
			}
		}
		return gateways;
	}
	
	public List<MessagingGateway> getMessagingGatewaysForAddress(String address){
		MessagingAddress a = Context.getService(MessagingAddressService.class).getMessagingAddress(address);
		if(a!=null){
			return getMessagingGatewaysForAddress(a);
		}else{
			return new ArrayList<MessagingGateway>();
		}
	}
	
	/**
	 * @param messagingGatewayClass the gateway's class
	 * @return The address factory for that gateway
	 */
	public AddressFactory getAddressFactoryForGateway(Class messagingGatewayClass) {
		try{
			return getMessagingGateway(messagingGatewayClass).getAddressFactory();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * @param messagingGatewayClass the gateway's class
	 * @return the message factory for that gateway
	 */
	public MessageFactory getMessageFactoryForGateway(Class messagingGatewayClass) {
		try{
			return getMessagingGateway(messagingGatewayClass).getMessageFactory();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * @return All message factories
	 */
	public Set<MessageFactory> getMessageFactories(){
		HashSet<MessageFactory> factories = new HashSet<MessageFactory>();
		for(MessagingGateway service:gateways){
			factories.add(service.getMessageFactory());
		}
		return factories;
	}
	
	/**
	 * @return All address factories
	 */
	public Set<AddressFactory> getAddressFactories(){
		HashSet<AddressFactory> factories = new HashSet<AddressFactory>();
		for(MessagingGateway service:gateways){
			factories.add(service.getAddressFactory());
		}
		return factories;
	}

	/**
	 * @param name
	 * @return The address factory that supports the address type represented by the parameter
	 */
	public AddressFactory getAddressFactoryForAddressTypeName(String name){
		for(AddressFactory af: getAddressFactories()){
			try {
				if(((MessagingAddress) af.getAddressClass().newInstance()).getName().equalsIgnoreCase(name)){
					return af;
				}
			} catch (Exception e) {}
		}
		return null;
	}
	
	public Class getAddressClassForAddressTypeName(String name){
		for(AddressFactory af: getAddressFactories()){
			try {
				if(((MessagingAddress) af.getAddressClass().newInstance()).getName().equalsIgnoreCase(name)){
					return af.getAddressClass();
				}
			} catch (Exception e) {}
		
		}
		return null;
	}
	
	/**
	 * @param addressClass
	 * @return The address factory that produces the provided addresses
	 */
	public AddressFactory getAddressFactoryForAddressClass(Class addressClass){
		for(AddressFactory af: getAddressFactories()){
			if(addressClass.equals(af.getAddressClass())){
				return af;
			}
		}
		return null;
	}
	
	public Set<String> getAddressTypes(){
		HashSet<String> set = new HashSet<String>();
		for(AddressFactory af: getAddressFactories()){
			try {
				Class maClass =  af.getAddressClass();
				MessagingAddress ma = (MessagingAddress) maClass.newInstance();
				String name = ma.getName();
				set.add(name);
			} catch (Exception e) {
				System.out.println("Shit");
			}
		}
		return set;
	}
}
