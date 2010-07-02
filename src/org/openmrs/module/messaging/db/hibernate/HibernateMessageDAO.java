package org.openmrs.module.messaging.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessageStatus;
import org.openmrs.module.messaging.schema.Protocol;

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
		return (Message) sessionFactory.getCurrentSession().createCriteria(Message.class).add(Restrictions.eq("messageId",messageId)).list();
	}
	
	public List<Message> findMessagesWithAddresses(Protocol protocol, String toAddress,String fromAddress, String content,Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(protocol !=null){
			c.add(Restrictions.eq("protocolId",protocol.getProtocolId()));
		}
		if(toAddress!= null && !toAddress.equals("")){
			c.add(Restrictions.eq("origin", toAddress));
		}
		if(fromAddress!= null && !fromAddress.equals("")){
			c.add(Restrictions.eq("destination", fromAddress));
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like("content", "%"+content+"%"));
		}
		if(status != null){
			c.add(Restrictions.eq("status", status));
		}
		return c.list();
	}

	public List<Message> findMessagesWithPeople(Protocol protocol, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(protocol !=null){
			c.add(Restrictions.eq("protocolId",protocol.getProtocolId()));
		}
		if(sender!= null){
			c.add(Restrictions.eq("sender", sender));
		}
		if(recipient!= null){
			c.add(Restrictions.eq("recipient", recipient));
		}
		if(content!= null && !content.equals("")){
			c.add(Restrictions.like("content", "%"+content+"%"));
		}
		if(status != null){
			c.add(Restrictions.eq("status", status));
		}
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
		c.add(Restrictions.eq("status", MessageStatus.OUTBOX));
		return c.list();
	}

	public List<Message> getOutboxMessagesByProtocol(Protocol p) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq("status", MessageStatus.OUTBOX));
		c.add(Restrictions.eq("protocolId", p.getProtocolId()));
		return c.list();
	}
	
}
