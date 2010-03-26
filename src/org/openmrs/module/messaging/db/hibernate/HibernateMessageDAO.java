package org.openmrs.module.messaging.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.db.MessageDAO;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

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
		ArrayList<String> addresses= new ArrayList<String>();
		for(MessagingAddress address: ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPerson(sender)){
			addresses.add(address.getAddress());
		}
		c.add(Restrictions.in("origin", addresses));
		return c.list();
	}
	
	public List<Message> getMessagesToPerson(Person recipient){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		ArrayList<String> addresses= new ArrayList<String>();
		for(MessagingAddress address: ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPerson(recipient)){
			addresses.add(address.getAddress());
		}
		c.add(Restrictions.in("destination", addresses));
		return c.list();
	}
	
	public List<Message> getMessagesToOrFromPerson(Person person){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		ArrayList<String> addresses= new ArrayList<String>();
		for(MessagingAddress address: ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPerson(person)){
			addresses.add(address.getAddress());
		}
		c.add(Restrictions.or(Restrictions.in("destination", addresses),Restrictions.in("origin", addresses)));
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
	
	public List<Message> getMessagesForService(MessagingService service){
		return sessionFactory.getCurrentSession().createCriteria(service.getMessageClass()).list();
	}
	

	public List<Message> getMessagesToPersonUsingService(MessagingService service, Person recipient){
		return findMessagesWithPeople(service,null,recipient,null,null);
	}

	public List<Message> getMessagesFromPersonUsingService(MessagingService service, Person sender){
		return findMessagesWithPeople(service,sender,null,null,null);
	}

	public List<Message> getMessagesToOrFromPersonUsingService(MessagingService service, Person person){
		return findMessagesWithPeople(service, person, person, null, null);
	}

	public List<Message> findMessages(String content){
		return findMessagesWithAddresses(null,null,null,content,null);
	}
	
	public List<Message> findMessagesWithAddresses(MessagingService service, String toAddress,String fromAddress, String content,Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(service !=null){
			c = sessionFactory.getCurrentSession().createCriteria(service.getMessageClass());
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

	public List<Message> findMessagesWithPeople(MessagingService service, Person sender, Person recipient, String content, Integer status){
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Message.class);
		if(service !=null){
			c = sessionFactory.getCurrentSession().createCriteria(service.getMessageClass());
		}
		if(sender!= null){
			ArrayList<String> fromAddresses= new ArrayList<String>();
			for(MessagingAddress address: ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPerson(sender)){
				fromAddresses.add(address.getAddress());
			}
			c.add(Restrictions.in("origin",fromAddresses));
		}
		if(recipient!= null){
			ArrayList<String> toAddresses= new ArrayList<String>();
			for(MessagingAddress address: ((MessagingAddressService) Context.getService(MessagingAddressService.class)).getMessagingAddressesForPerson(recipient)){
				toAddresses.add(address.getAddress());
			}
			c.add(Restrictions.in("origin",toAddresses));
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
