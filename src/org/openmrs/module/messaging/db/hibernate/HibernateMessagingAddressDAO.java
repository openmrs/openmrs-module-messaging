package org.openmrs.module.messaging.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

public class HibernateMessagingAddressDAO implements MessagingAddressDAO {

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
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> getAllMessagingAddresses() {
		return sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class).list();
	}
	
	public MessagingAddress getMessagingAddress(Integer addressId) {
		return (MessagingAddress) sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class).add(Restrictions.eq("addressId", addressId)).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> getMessagingAddressesForService(MessagingService service) {
		return sessionFactory.getCurrentSession().createCriteria(service.getMessagingAddressClass()).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("person", person));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> getMessagingAddressesForPersonAndService(Person person, MessagingService service) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(service.getMessagingAddressClass());
		if(person !=null){
			c.add(Restrictions.eq("person", person));
		}
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> findMessagingAddresses(String search) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.like("address","%"+search+"%"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingService service) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(service.getMessagingAddressClass());
		c.add(Restrictions.like("address","%"+search+"%"));
		return c.list();
	}

	public MessagingAddress getPreferredMessagingAddressForPerson(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

	//CRUD
	
	public void deleteMessagingAddress(MessagingAddress address) throws DAOException{
		sessionFactory.getCurrentSession().delete(address);
	}

	public void voidMessagingAddress(MessagingAddress address, String reason) throws APIException {
		address.setVoided(true);
		address.setVoidedBy(Context.getAuthenticatedUser());
		address.setVoidReason(reason);
		sessionFactory.getCurrentSession().saveOrUpdate(address);
	}

	public void saveMessagingAddress(MessagingAddress address) throws DAOException{
		sessionFactory.getCurrentSession().save(address);
	}

	public void unvoidMessagingAddress(MessagingAddress address) throws DAOException{
		address.setVoided(false);
		sessionFactory.getCurrentSession().saveOrUpdate(address);
	}

}
