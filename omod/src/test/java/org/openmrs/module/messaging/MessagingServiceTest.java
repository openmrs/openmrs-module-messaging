package org.openmrs.module.messaging;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.email.EmailProtocol;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MessagingServiceTest extends BaseModuleContextSensitiveTest{

	@Before
	public void beforeAllTests(){
		try {
			//initializeInMemoryDatabase();
			executeDataSet("datasets/messaging_data_set2.xml");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void createMessageTest(){
		Message m = new Message("Test","dieterich@medicmobile.org",EmailProtocol.class);
		Assert.assertNotNull("Content Null",m.getContent());
		Assert.assertNotNull("To list Null",m.getTo());
		MessageRecipient mr = (MessageRecipient) m.getTo().toArray()[0];
		Assert.assertNotNull("Mesage Recipient Null",mr);
		Assert.assertNotNull("Mesage Recipient Address Null",mr.getRecipient());
		Assert.assertNotNull("Mesage Recipient Address Text Null",mr.getRecipient().getAddress());
	}
	
	@Test
	public void sendMessageTest(){
		Message m = new Message("Test","dieterich@medicmobile.org",EmailProtocol.class);
		Context.getService(MessageService.class).saveMessage(m);
		
		Assert.assertNotNull("Content Null",m.getContent());
		Assert.assertNotNull("To list Null",m.getTo());
		MessageRecipient mr = (MessageRecipient) m.getTo().toArray()[0];
		Assert.assertNotNull("Mesage Recipient Null",mr);
		Assert.assertNotNull("Mesage Recipient Address Null",mr.getRecipient());
		Assert.assertNotNull("Mesage Recipient Address Text Null",mr.getRecipient().getAddress());
		Assert.assertNotNull("Mesage Recipient uuid Null",mr.getRecipient().getUuid());
	}
	
	
	
}
