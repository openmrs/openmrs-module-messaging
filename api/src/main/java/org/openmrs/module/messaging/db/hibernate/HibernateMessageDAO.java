package org.openmrs.module.messaging.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.Message.MessageFields;
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
		return (Message) sessionFactory.getCurrentSession().createCriteria(Message.class).add(Restrictions.eq(MessageFields.MESSAGE_ID.fieldName,messageId)).list();
	}
	
	public List<Message> findMessagesWithAddresses(Class<? extends Protocol> protocolClass, String toAddress, String fromAddress, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(protocolClass !=null){
			c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.fieldName,protocolClass.getName()));
		}
		if(toAddress!= null && !toAddress.equals("") && toAddress.equals(fromAddress)){
			c.createAlias(MessageFields.TO.fieldName, "tos");
			c.add(Restrictions.or(Restrictions.eq(MessageFields.ORIGIN.fieldName,fromAddress), Restrictions.eq("tos.address",toAddress)));
		}else{
			if(toAddress!= null && !toAddress.equals("")){
				c.createCriteria(MessageFields.TO.fieldName).add(Restrictions.eq("address", toAddress));
			}
			if(fromAddress!= null && !fromAddress.equals("")){
				c.add(Restrictions.eq(MessageFields.ORIGIN.fieldName, fromAddress));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.fieldName, "%"+content+"%"));
		}
		if(status != null){
			c.add(Restrictions.eq(MessageFields.STATUS.fieldName, status));
		}
		return c.list();
	}
	
	

	public List<Message> findMessagesWithPeople(Class<? extends Protocol> protocolClass, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(protocolClass !=null){
			c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.fieldName,protocolClass.getName()));
		}
		if(sender != null && sender.equals(recipient)){
			c.createAlias(MessageFields.TO.fieldName, "tos");
			c.add(Restrictions.or(Restrictions.eq(MessageFields.SENDER.fieldName,sender), Restrictions.eq("tos.person",recipient)));
		}else{
			if(sender!= null){
				c.add(Restrictions.eq(MessageFields.SENDER.fieldName, sender));
			}
			if(recipient!= null){
				c.createCriteria(MessageFields.TO.fieldName).add(Restrictions.eq("person", recipient));
			}
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like(MessageFields.CONTENT.fieldName, "%"+content+"%"));
		}
		if(status != null){
			c.add(Restrictions.eq(MessageFields.STATUS.fieldName, status));
		}
		c.addOrder(Order.asc(MessageFields.DATE.fieldName));
		return c.list();
	}


	public void deleteMessage(Message message) {
		sessionFactory.getCurrentSession().delete(message);
	}

	public void saveMessage(Message message) {
		if(message.getOrigin() !=null){ 
			System.out.println("FROM ADDRESS SET before DAO: "+ message.getOrigin());
		}else{
			System.out.println("Origin is null before DAO");
		}
		if(message.getSender()!= null){
			System.out.println("FROM PERSON SET before DAO: " + message.getSender().getPersonName().toString());
		}else{
			System.out.println("SENDER IS NULL before DAO");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(message);
		if(message.getOrigin() !=null){ 
			System.out.println("FROM ADDRESS SET after DAO: "+ message.getOrigin());
		}else{
			System.out.println("Origin is null after DAO");
		}
		if(message.getSender()!= null){
			System.out.println("FROM PERSON SET after DAO: " + message.getSender().getPersonName().toString());
		}else{
			System.out.println("SENDER IS NULL after DAO");
		}
	}

	public List<Message> getOutboxMessages() {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq(MessageFields.STATUS.fieldName, MessageStatus.OUTBOX.getNumber()));
		return c.list();
	}

	public List<Message> getOutboxMessagesByProtocol(Class<? extends Protocol> protocolClass) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.or(Restrictions.eq(MessageFields.STATUS.fieldName, MessageStatus.OUTBOX.getNumber()),Restrictions.eq(MessageFields.STATUS.fieldName, MessageStatus.RETRYING.getNumber())));
		c.add(Restrictions.eq(MessageFields.PROTOCOL_CLASS.fieldName, protocolClass.getName()));
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
	public List<Message> getMessagesForPersonPaged(int pageNumber, int pageSize, int personId, boolean to){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(pageNumber >-1){
			c.setFirstResult(pageNumber * pageSize);
			c.setMaxResults(pageSize);
		}
		Person p = Context.getPersonService().getPerson(personId);
		if(p != null){
			if(to == true){
				List<Message> messages = c.list();
				List<Message> result = new ArrayList<Message>();
				for(Message m: messages){
					for(MessageRecipient recipient: m.getTo()){
						if(recipient.getRecipient().getPerson().equals(p)){
							result.add(m);
							continue;
						}
					}
				}
				return result;
			}else{
				c.add(Restrictions.eq(MessageFields.SENDER.fieldName, p));
			}
		}
		return c.list();
	}
}
