package org.openmrs.module.messaging.schema;

import org.openmrs.Person;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;

/**
 * An abstract superclass that represents a service that can send and receive
 * messages.
 */
public abstract class MessagingGateway {

	protected MessagingAddressService addressService;
	
	protected MessageService messageService;
	
	public abstract void sendMessage(Message message);

	public abstract boolean shouldSendMessage(Message m);
	
	/**
	 * Should return the default sender address of this messaging service. This
	 * would most likely be the address from which OpenMRS "sends" messages like
	 * an official twitter feed.
	 * 
	 * @return the default address
	 */
	public abstract MessagingAddress getDefaultSenderAddress();

	/**
	 * Should return true if the messaging service has the ability to send
	 * messages
	 * 
	 * @return
	 */
	public abstract boolean canSend();

	/**
	 * Should return true if the messaging service has the ability to receive
	 * messages
	 * 
	 * @return
	 */
	public abstract boolean canReceive();
	
	public abstract boolean isActive();

	/**
	 * Should perform all necessary operations to start the Gateway so that
	 * either canSend or canReceive returns true (ideally both)
	 */
	public abstract void startup();

	/**
	 * Should perform all necessary operations to stop the Gateway. Both
	 * canRecieve and canSend should return false.
	 */
	public abstract void shutdown();

	/**
	 * Should return the display name of this messaging gateway. This name will
	 * be used in UI and should be internationalized
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Should return a short description of the messaging gateway
	 * 
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Should return a boolean representing whether or not this gateway can send
	 * messages from user addresses for the given protocol. Would return false if all messages
	 * must be routed from a single origin, the default sender address. An example of this would be 
	 * an SMS modem connected to the OpenMRS server - it's impossible to send SMS's from user's
	 * phone numbers 
	 */
	public abstract boolean canSendFromUserAddresses(Protocol protocol);

	/**
	 * Should return true if this gateway supports sending messages using the
	 * provided protocol. With this method it is possible for gateways to
	 * support more than one protocol at once.
	 * 
	 * @param protocol
	 * @return
	 */
	public abstract boolean supportsProtocol(Protocol p);
	
	protected void receiveMessage(String toAddress, String fromAddress, String content, String protocolId){
		Message m = new Message(toAddress,content);
		m.setOrigin(fromAddress);
		Person sender = addressService.getPersonForAddress(fromAddress);
		Person recipient = addressService.getPersonForAddress(toAddress);
		m.setSender(sender);
		m.setRecipient(recipient);
		m.setStatus(MessageStatus.RECEIVED);
		m.setProtocolId(protocolId);
		messageService.saveMessage(m);
	}

}
