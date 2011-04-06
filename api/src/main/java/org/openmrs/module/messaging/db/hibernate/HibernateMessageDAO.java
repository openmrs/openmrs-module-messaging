package org.openmrs.module.messaging.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.Message.MessageFields;
import org.openmrs.module.messaging.domain.MessageRecipient.MessageRecipientFields;
import org.openmrs.module.messaging.domain.MessagingAddress.MessagingAddressFields;
import org.openmrs.module.messaging.domain.gateway.Protocol;

public class HibernateMessageDAO implements MessageDAO {

	protected Log log = LogFactory.getLog(getClass());
	
	
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
		if(protocolClass !=null){
			c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.name,protocolClass.getName()));
		}
		if(toAddress!= null && !toAddress.equals("") && toAddress.equals(fromAddress)){
			c.createAlias(MessageFields.TO.name, "tos");
			c.add(Restrictions.or(Restrictions.eq(MessageFields.ORIGIN.name,fromAddress), Restrictions.eq("tos.address",toAddress)));
		}else{
			if(toAddress!= null && !toAddress.equals("")){
				c.createCriteria(MessageFields.TO.name).createCriteria("recipient").add(Restrictions.eq("address",toAddress));
			}
			if(fromAddress!= null && !fromAddress.equals("")){
				c.add(Restrictions.eq(MessageFields.ORIGIN.name, fromAddress));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.name, content,MatchMode.ANYWHERE));
		}
		if(status != null){
			c.add(Restrictions.eq(MessageFields.STATUS.name, status));
		}
		return c.list();
	}
	
	

	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocolClass, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(protocolClass !=null){
			c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.name,protocolClass.getName()));
		}
		if(sender != null && sender.equals(recipient)){
			c.createAlias("to.recipient.person", "recipients");
			c.add(Restrictions.or(Restrictions.eq(MessageFields.SENDER.name,sender), Restrictions.eq("recipients",recipient)));
		}else{
			if(sender!= null){
				c.add(Restrictions.eq(MessageFields.SENDER.name, sender));
			}
			if(recipient!= null){
				c.createCriteria(MessageFields.TO.name).createCriteria("recipient").add(Restrictions.eq("person", recipient));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.name, "%"+content+"%"));
		}
		if(status != null){
			c.add(Restrictions.eq(MessageFields.STATUS.name, status));
		}
		c.addOrder(Order.asc(MessageFields.DATE.name));
		return c.list();
	}


	public void deleteMessage(Message message) {
		sessionFactory.getCurrentSession().delete(message);
	}

	public void saveMessage(Message message) {
		sessionFactory.getCurrentSession().saveOrUpdate(message);
	}

	public List<Message> getOutboxMessages() {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq(MessageFields.STATUS.name, MessageStatus.OUTBOX.getNumber()));
		return c.list();
	}

	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocolClass) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.or(Restrictions.eq(MessageFields.STATUS.name, MessageStatus.OUTBOX.getNumber()),Restrictions.eq(MessageFields.STATUS.name, MessageStatus.RETRYING.getNumber())));
		c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.name, protocolClass.getName()));
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
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to, boolean orderDateAscending){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(pageNumber >-1){
			c.setFirstResult(pageNumber * pageSize);
			c.setMaxResults(pageSize);
		}
		Person p = Context.getPersonService().getPerson(personId);
		if(p != null){
			if(to){
				c.createCriteria(MessageFields.TO.name).
					createCriteria(MessageRecipientFields.RECIPIENT.name).
					add(Restrictions.eq(MessagingAddressFields.PERSON.name, p));
			}else{
				c.add(Restrictions.eq(MessageFields.SENDER.name, p));
			}
		}
		if(!orderDateAscending) c.addOrder(Order.desc(MessageFields.DATE.name));
		else c.addOrder(Order.asc(MessageFields.DATE.name));
		
		return c.list();
	}
	
	public Integer countMessagesForPerson(int personId, boolean to){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		Person p = Context.getPersonService().getPerson(personId);
		if(p != null){
			if(to == true){
				c.createCriteria("to").add(Restrictions.eq("recipient", p));
			}else{
				c.add(Restrictions.eq(MessageFields.SENDER.name, p));
			}
		}
		c.setProjection(Projections.rowCount());
		return (Integer) c.uniqueResult();
	}
}
