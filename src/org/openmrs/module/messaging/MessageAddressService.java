package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.messaging.schema.MessageAddress;
import org.openmrs.module.messaging.schema.MessagingService;

public interface MessageAddressService {

	public MessageAddress getPreferredMessagingAddressForPerson(Person person);
	
	public List<MessageAddress> getAllMessagingAddressesForPerson(Person person);
	
	public List<MessageAddress> getMessagingAddressesForService(MessagingService service);
	
	public List<MessageAddress> getMessagingAddressesForPersonAndService(Person person, MessagingService service);
	
}
