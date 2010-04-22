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
import org.openmrs.module.messaging.schema.MessagingGateway;

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
	
	public List<Message> getMessagesFromPerson(Person sender){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq("sender", sender));
		return c.list();
	}
	
	public List<Message> getMessagesToPerson(Person recipient){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq("recipient", recipient));
		return c.list();
	}
	
	public List<Message> getMessagesToOrFromPerson(Person person){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.or(Restrictions.eq("sender", person),Restrictions.eq("recipient", person)));
		return c.list();

	}
	
	public List<Message> getMessagesFromAddress(String address){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq("origin", address));
		return c.list();
	}

	public List<Message> getMessagesToAddress(String address){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.eq("destination", address));
		return c.list();
	}

	public List<Message> getMessagesToOrFromAddress(String address){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		c.add(Restrictions.or(Restrictions.eq("destination", address),Restrictions.eq("origin", address)));
		return c.list();
	}
	
	public List<Message> getMessagesForGateway(MessagingGateway gateway){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(gateway.getMessageClass());
		c.add(Restrictions.eq("gatewayId",gateway.getGatewayId()));
		return c.list();
	}
	

	public List<Message> getMessagesToPersonUsingGateway(MessagingGateway gateway, Person recipient){
		return findMessagesWithPeople(gateway,null,recipient,null,null);
	}

	public List<Message> getMessagesFromPersonUsingGateway(MessagingGateway gateway, Person sender){
		return findMessagesWithPeople(gateway,sender,null,null,null);
	}

	public List<Message> getMessagesToOrFromPersonUsingGateway(MessagingGateway gateway, Person person){
		return findMessagesWithPeople(gateway, person, person, null, null);
	}

	public List<Message> findMessages(String content){
		return findMessagesWithAddresses(null,null,null,content,null);
	}
	
	public List<Message> findMessagesWithAddresses(MessagingGateway gateway, String toAddress,String fromAddress, String content,Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(gateway !=null){
			c = sessionFactory.getCurrentSession().createCriteria(gateway.getMessageClass());
			c.add(Restrictions.eq("gatewayId",gateway.getGatewayId()));
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

	public List<Message> findMessagesWithPeople(MessagingGateway gateway, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(gateway !=null){
			c = sessionFactory.getCurrentSession().createCriteria(gateway.getMessageClass());
			c.add(Restrictions.eq("gatewayId",gateway.getGatewayId()));
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
	
}
