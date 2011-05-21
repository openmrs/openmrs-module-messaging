package org.openmrs.module.messaging.db.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.hibernate.transform.RootEntityResultTransformer;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.Message.MessageFields;
import org.openmrs.module.messaging.domain.MessageRecipient.MessageRecipientFields;
import org.openmrs.module.messaging.domain.MessagingAddress.MessagingAddressFields;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.omail.OMailProtocol;

public class HibernateMessageDAO implements MessageDAO {

	protected Log log = LogFactory.getLog(getClass());
	
	private Set<Integer> seenMessages = new HashSet<Integer>();
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}
	
	public List<Message> getAllMessages(){
		return sessionFactory.getCurrentSession().createCriteria(Message.class).list();
	}
	
	public Message getMessage(Integer messageId){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq(MessageFields.MESSAGE_ID.name,messageId));
		return (Message) c.uniqueResult();
	}
	
	public List<Message> findMessagesWithAddresses(Class<? extends Protocol> protocolClass, String toAddress, String fromAddress, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		Criteria toCrit = c.createCriteria(MessageFields.TO.name);
		Criteria recipientCrit = toCrit.createCriteria(MessageRecipientFields.RECIPIENT.name);
		if(protocolClass !=null){
			recipientCrit.add(Restrictions.eq(MessagingAddressFields.PROTOCOL.name,protocolClass.getName()));
		}
		if(toAddress!= null && !toAddress.equals("") && toAddress.equals(fromAddress)){
//			c.createAlias(MessageFields.TO.name, "tos");
//			c.add(Restrictions.or(Restrictions.eq(MessageFields.ORIGIN.name,fromAddress), Restrictions.eq("tos.address",toAddress)));
		}else{
			if(toAddress!= null && !toAddress.equals("")){
				recipientCrit.add(Restrictions.eq(MessagingAddressFields.ADDRESS.name,toAddress));
			}
			if(fromAddress!= null && !fromAddress.equals("")){
				toCrit.add(Restrictions.eq(MessageRecipientFields.ORIGIN.name, fromAddress));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.name, content,MatchMode.ANYWHERE));
		}
		if(status != null){
			toCrit.add(Restrictions.eq(MessageRecipientFields.STATUS.name, status));
		}
		c.setResultTransformer(new DistinctRootEntityResultTransformer());
		return c.list();
	}
	
	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocolClass, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		Criteria toCrit = c.createCriteria(MessageFields.TO.name);
		Criteria recipientCrit = toCrit.createCriteria(MessageRecipientFields.RECIPIENT.name);
		if(protocolClass !=null){
			recipientCrit.add(Restrictions.eq(MessagingAddressFields.PROTOCOL.name,protocolClass.getName()));
		}
		if(sender != null && sender.equals(recipient)){
			//c.createAlias("to.recipient.person", "recipients");
			//c.add(Restrictions.or(Restrictions.eq(MessageFields.SENDER.name,sender), Restrictions.eq("recipients",recipient)));
		}else{
			if(sender!= null){   
				c.add(Restrictions.eq(MessageFields.SENDER.name, sender));
			}
			if(recipient!= null){
				recipientCrit.add(Restrictions.eq(MessagingAddressFields.PERSON.name, recipient));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.name, content,MatchMode.ANYWHERE));
		}
		if(status != null){
			toCrit.add(Restrictions.eq(MessageRecipientFields.STATUS.name, status));
		}
		c.addOrder(Order.asc(MessageFields.DATE.name));
		c.setResultTransformer(new DistinctRootEntityResultTransformer());
		return c.list();
	}

	public void deleteMessage(Message message) {
		sessionFactory.getCurrentSession().delete(message);
	}

	public void saveMessage(Message message) {
		for(MessageRecipient m: message.getTo()){
			if(m.getMessageStatus() != null && m.getMessageStatus() == MessageStatus.RECEIVED && !seenMessages.contains(message.getId())){
				Context.getService(MessagingService.class).notifyListeners(message);
				if(message.getId() > 0){
					seenMessages.add(message.getId());
				}
				break;
			}
		}
		sessionFactory.getCurrentSession().saveOrUpdate(message);
	}

	public List<Message> getOutboxMessages() {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.createCriteria(MessageFields.TO.name)
		.add(Restrictions.or(
				Restrictions.eq(MessageRecipientFields.STATUS.name, MessageStatus.OUTBOX.getNumber()),
				Restrictions.eq(MessageRecipientFields.STATUS.name, MessageStatus.RETRYING.getNumber())));
		return c.list();
	}

	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocolClass) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.createCriteria(MessageFields.TO.name)
		.add(Restrictions.or(
				Restrictions.eq(MessageRecipientFields.STATUS.name, MessageStatus.OUTBOX.getNumber()),
				Restrictions.eq(MessageRecipientFields.STATUS.name, MessageStatus.RETRYING.getNumber())))
		.createCriteria(MessageRecipientFields.RECIPIENT.name).
			add(Restrictions.eq(MessagingAddressFields.PROTOCOL.name, protocolClass.getName()));
		return c.list();
	}
	
	/**
	 * Returns the messages to or from a person with several optional parameters. Results can be paged.
	 * @param pageNumber The page number of the results. -1 returns all results.
	 * @param pageSize The page size of the results.
	 * @param personId The person that the messages are to or from.
	 * @param to If true, this method fetches the messages to the person, otherwise it fetches the messages from the person 
	 * @return
	 */
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to, boolean orderDateAscending, Class<? extends Protocol> protocolClass){
		Criteria c = getMessagesForPersonCriteria(personId, to, protocolClass);
		if(pageNumber >-1){
			c.setFirstResult(pageNumber * pageSize);
			c.setMaxResults(pageSize);
		}
		if(!orderDateAscending) c.addOrder(Order.desc(MessageFields.DATE.name));
		else c.addOrder(Order.asc(MessageFields.DATE.name));
		c.setResultTransformer(new DistinctRootEntityResultTransformer());
		return c.list();
	}
	
	public Integer countMessagesForPerson(int personId, boolean to, Class<? extends Protocol> protocolClass){
		Criteria c = getMessagesForPersonCriteria(personId, to, protocolClass);
		c.setProjection(Projections.countDistinct(MessageFields.MESSAGE_ID.name));
		return ((Integer) c.uniqueResult());
	}
	
	private Criteria getMessagesForPersonCriteria(int personId, boolean to, Class<? extends Protocol> protocolClass){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		Person p = Context.getPersonService().getPerson(personId);
		if(p != null){
			if(to){
				Criteria recipientCrit = c.createCriteria(MessageFields.TO.name).createCriteria(MessageRecipientFields.RECIPIENT.name);
				if(protocolClass != null){
					recipientCrit.add(Restrictions.and(
							Restrictions.eq(MessagingAddressFields.PERSON.name, p),
							Restrictions.eq(MessagingAddressFields.PROTOCOL.name, protocolClass.getName())));
				}else{
					recipientCrit.add(Restrictions.eq(MessagingAddressFields.PERSON.name, p));
				}
			}else{
				//TODO figure out the proper restrictions here
				c.add(Restrictions.eq(MessageFields.SENDER.name, p));
			}
		}
		return c;
	}
	
	public List<Message> searchMessages(int pageNumber, int pageSize, String searchString, Person p, boolean inbox, boolean outbox, boolean orderDateAscending){
		Criteria c = getSearchCriteria(p, searchString, inbox, outbox);
		if(pageNumber >-1){
			c.setFirstResult(pageNumber * pageSize);
			c.setMaxResults(pageSize);
		}
		if(!orderDateAscending) c.addOrder(Order.desc(MessageFields.DATE.name));
		else c.addOrder(Order.asc(MessageFields.DATE.name));
		c.setResultTransformer(new DistinctRootEntityResultTransformer());
		return c.list();
	}

	public Integer countSearch(Person p, String searchString, boolean inbox, boolean outbox) {
		Criteria c = getSearchCriteria(p, searchString, inbox, outbox);
		c.setProjection(Projections.countDistinct(MessageFields.MESSAGE_ID.name));
		return (Integer) c.uniqueResult();
	}
	
	private Criteria getSearchCriteria(Person p, String searchString, boolean inbox, boolean outbox){
		//create criteria and an alias
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		//add the content and subject restrictions
		Criterion content = Restrictions.like(MessageFields.CONTENT.name,searchString,MatchMode.ANYWHERE);
		Criterion subject = Restrictions.like(MessageFields.SUBJECT.name, searchString,MatchMode.ANYWHERE);
		c.add(Restrictions.disjunction().add(content).add(subject));
		//TODO add person name searching
		//if we're searching the inbox and outbox, add an OR query
		if(inbox && outbox){
			c.createAlias("to.recipient", "recp");
			c.add(Restrictions.or(
					Restrictions.eq("recp.person",p),
					Restrictions.eq(MessageFields.SENDER.name,p)
			));
		}else if(inbox){
			Criteria recipientCrit = c.createCriteria(MessageFields.TO.name).createCriteria(MessageRecipientFields.RECIPIENT.name);
			recipientCrit.add(Restrictions.and(
					Restrictions.eq(MessagingAddressFields.PERSON.name, p),
					Restrictions.eq(MessagingAddressFields.PROTOCOL.name, OMailProtocol.class.getName())));
		}else if(outbox){
			c.add(Restrictions.eq(MessageFields.SENDER.name,p));
		}
		return c;
	}
}