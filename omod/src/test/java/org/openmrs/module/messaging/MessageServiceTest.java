package org.openmrs.module.messaging;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MessageServiceTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeAllTests(){
		try {
			//initializeInMemoryDatabase();
			executeDataSet("datasets/messaging_data_set.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see MessageService#countMessagesForPerson(int,boolean)
	 * @verifies return number of messages to or from a person
	 */
	@Test
	public void countMessagesForPerson_shouldReturnNumberOfMessagesToOrFromAPerson() throws Exception {
		int count = Context.getService(MessageService.class).countMessagesForPerson(1, true);
		Assert.assertEquals(18, count);
		int count2 = Context.getService(MessageService.class).countMessagesForPerson(5, true);
		Assert.assertEquals(17, count2);
		int count3 = Context.getService(MessageService.class).countMessagesForPerson(6, true);
		Assert.assertEquals(29, count3);
	}

	/**
	 * @see MessageService#findMessages(String)
	 * @verifies return all messages with supplied string in message content
	 */
	@Test
	public void findMessages_shouldReturnAllMessagesWithSuppliedStringInMessageContent()throws Exception {
		List<Message> helloMessages = getMessageService().findMessages("hello");
		Assert.assertEquals(1,helloMessages.size());
		Assert.assertEquals((Integer) 6, helloMessages.get(0).getId());
		List<Message> loremMessages = getMessageService().findMessages("lorem");
		Assert.assertEquals(2,loremMessages.size());
		Assert.assertEquals((Integer) 13, loremMessages.get(0).getId());
		Assert.assertEquals((Integer) 14, loremMessages.get(1).getId());
		List<Message> aMessages = getMessageService().findMessages("a");
		int[] ids = {1,7,8,9,10,11,12,13,14};
		Assert.assertEquals(9, aMessages.size());
		for(int i = 0; i < 9; i++){
			Assert.assertEquals((Integer) ids[i], aMessages.get(i).getId());
		}
	}

	/**
	 * @see MessageService#findMessagesWithAdresses(Class,String,String,String,Integer)
	 * @verifies perform an OR query when to and from addresses are present
	 */
	@Test
	@Ignore
	public void findMessagesWithAdresses_shouldPerformAnORQueryWhenToAndFromAddressesArePresent() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#findMessagesWithPeople(Class,Person,Person,String,Integer)
	 * @verifies perform an OR query when to and from people are present
	 */
	@Test
	@Ignore
	public void findMessagesWithPeople_shouldPerformAnORQueryWhenToAndFromPeopleArePresent()
			throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getAllMessages()
	 * @verifies return all messages
	 */
	@Test
	public void getAllMessages_shouldReturnAllMessages() throws Exception {
		List<Message> messages = Context.getService(MessageService.class).getAllMessages();
		Assert.assertEquals(messages.size(), 36);
	}

	/**
	 * @see MessageService#getMessage(Integer)
	 * @verifies return message with given id
	 */
	@Test
	public void getMessage_shouldReturnMessageWithGivenId() throws Exception {
		Message m = getMessageService().getMessage(1);
		Assert.assertEquals((Integer) 1, m.getId());
	}

	/**
	 * @see MessageService#getMessagesForAddress(String,boolean)
	 * @verifies return all messages from address if to is false
	 */
	@Test
	public void getMessagesForAddress_shouldReturnAllMessagesFromAddressIfToIsFalse() throws Exception {
		List<Message> messagesFromAddress = getMessageService().getMessagesForAddress("5", false);
		Assert.assertEquals(23, messagesFromAddress.size());
		int[] ids = {14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};
		checkMessageIds(ids, messagesFromAddress);
	}
	/**
	 * @see MessageService#getMessagesForAddress(String,boolean)
	 * @verifies return all messages to address if to is true
	 */
	@Test
	public void getMessagesForAddress_shouldReturnAllMessagesToAddressIfToIsTrue() throws Exception {
		List<Message> messagesToAddress = getMessageService().getMessagesForAddress("4", true);
		Assert.assertEquals(20,messagesToAddress.size());
		int[] ids = {1,2,3,4,8,9,10,13,14,26,27,28,29,30,31,32,33,34,35,36};
		checkMessageIds(ids, messagesToAddress);
	}

	/**
	 * @see MessageService#getMessagesForPerson(Person,boolean)
	 * @verifies return all messages from person if to is false
	 */
	@Test
	public void getMessagesForPerson_shouldReturnAllMessagesFromPersonIfToIsFalse() throws Exception {
		Person p = Context.getPersonService().getPerson(1);
		List<Message> messagesFromPerson = getMessageService().getMessagesForPerson(p, false);
		Assert.assertEquals(5, messagesFromPerson.size());
		int[] ids = {1,2,3,4,5};
		checkMessageIds(ids, messagesFromPerson);
	}

	/**
	 * @see MessageService#getMessagesForPerson(Person,boolean)
	 * @verifies return all messages to person if to is true
	 */
	@Test
	public void getMessagesForPerson_shouldReturnAllMessagesToPersonIfToIsTrue() throws Exception {
		Person p = Context.getPersonService().getPerson(4);
		List<Message> messagesToPerson = getMessageService().getMessagesForPerson(p, true);
		Assert.assertEquals(20,messagesToPerson.size());
		int[] ids = {1,2,3,4,8,9,10,13,14,26,27,28,29,30,31,32,33,34,35,36};
		checkMessageIds(ids, messagesToPerson);
	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean)
	 * @verifies return messages to a person if to is true
	 */
	@Test
	@Ignore
	public void getMessagesForPersonPaged_shouldReturnMessagesToAPersonIfToIsTrue() throws Exception {

	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean)
	 * @verifies return messages from a person if to is false
	 */
	@Test
	@Ignore
	public void getMessagesForPersonPaged_shouldReturnMessagesFromAPersonIfToIsFalse() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean)
	 * @verifies not return more than pageSize messages
	 */
	@Test
	@Ignore
	public void getMessagesForPersonPaged_shouldNotReturnMoreThanPageSizeMessages()
			throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean)
	 * @verifies return arbitrary pages of data
	 */
	@Test
	public void getMessagesForPersonPaged_shouldReturnArbitraryPagesOfData() throws Exception {
		List<Message> messages = getMessageService().getMessagesForPersonPaged(0,2, 5, false, false);
		checkMessageIdsInOrder(new int[]{36,35}, messages);
		List<Message> messages2 = getMessageService().getMessagesForPersonPaged(0,5, 5, false, false);
		checkMessageIdsInOrder(new int[]{36,35,34,33,32}, messages2);
		List<Message> messages3 = getMessageService().getMessagesForPersonPaged(1,4, 5, false, false);
		checkMessageIdsInOrder(new int[]{32,31,30,29}, messages3);
		List<Message> messages4 = getMessageService().getMessagesForPersonPaged(2,3, 5, false, false);
		checkMessageIdsInOrder(new int[]{30,29,28}, messages4);
		List<Message> messages5 = getMessageService().getMessagesForPersonPaged(2,10, 5, false, false);
		checkMessageIdsInOrder(new int[]{16,15,14}, messages5);
	}

	/**
	 * @see MessageService#getMessagesForProtocolAndStatus(Class,Integer)
	 * @verifies return all messages with supplied status and protocol
	 */
	@Test
	@Ignore
	public void getMessagesForProtocolAndStatus_shouldReturnAllMessagesWithSuppliedStatusAndProtocol() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getMessagesToOrFromAddress(String)
	 * @verifies return all messages to or from address
	 */
	@Test
	@Ignore
	public void getMessagesToOrFromAddress_shouldReturnAllMessagesToOrFromAddress() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getMessagesToOrFromPerson(Person)
	 * @verifies return all messages to or from person
	 */
	@Test
	@Ignore
	public void getMessagesToOrFromPerson_shouldReturnAllMessagesToOrFromPerson() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getOutboxMessages()
	 * @verifies return all messages with status of outbox
	 */
	@Test
	@Ignore
	public void getOutboxMessages_shouldReturnAllMessagesWithStatusOfOutbox() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see MessageService#getOutboxMessagesByProtocol(Class)
	 * @verifies return all outbox messages for protocol
	 */
	@Test
	@Ignore
	public void getOutboxMessagesByProtocol_shouldReturnAllOutboxMessagesForProtocol() throws Exception {
		Assert.fail("Not yet implemented");
	}
	
	private MessageService getMessageService(){
		return Context.getService(MessageService.class);
	}

	private void checkMessageIds(int[] expected, List<Message> messages){
		Assert.assertEquals(expected.length, messages.size());
		Set<Integer> idSet = new HashSet<Integer>();
		for(int i = 0; i < expected.length; i++) idSet.add(expected[i]);
		for(Message m: messages){
			Assert.assertEquals(true, idSet.remove(m.getId()));
		}
		Assert.assertEquals(0,idSet.size());
	}
	
	private void checkMessageIdsInOrder(int[] expected, List<Message> messages){
		Assert.assertEquals(expected.length, messages.size());
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals((Integer) expected[i], messages.get(i).getId());
		}
	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean,boolean)
	 * @verifies return in descending order by date if dateOrderAscending is false
	 */
	@Test
	public void getMessagesForPersonPaged_shouldReturnInDescendingOrderByDateIfDateOrderAscendingIsFalse() throws Exception {
		List<Message> ascendingMessagesFrom = getMessageService().getMessagesForPersonPaged(0,10, 5, false, false);
		checkMessageIdsInOrder(new int[]{36,35,34,33,32,31,30,29,28,27}, ascendingMessagesFrom);
		List<Message> ascendingMessagesTo = getMessageService().getMessagesForPersonPaged(0,10, 5, true, false);
		checkMessageIdsInOrder(new int[]{35,34,33,32,31,30,29,28,27,26}, ascendingMessagesTo);
	}

	/**
	 * @see MessageService#getMessagesForPersonPaged(int,int,int,boolean,boolean)
	 * @verifies return in ascending order by date if dateOrderAscending is true
	 */
	@Test
	public void getMessagesForPersonPaged_shouldReturnInAscendingOrderByDateIfDateOrderAscendingIsTrue() throws Exception {
		List<Message> ascendingMessagesFrom = getMessageService().getMessagesForPersonPaged(0,10, 5, false, true);
		checkMessageIdsInOrder(new int[]{14,15,16,17,18,19,20,21,22,23}, ascendingMessagesFrom);
		List<Message> ascendingMessagesTo = getMessageService().getMessagesForPersonPaged(0,10, 5, true, true);
		checkMessageIdsInOrder(new int[]{1,2,6,7,10,13,14,26,27,28}, ascendingMessagesTo);
	}
}