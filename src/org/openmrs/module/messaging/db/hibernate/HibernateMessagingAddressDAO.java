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
import org.openmrs.module.messaging.schema.MessagingGateway;
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
	
	public List<MessagingAddress> getAllMessagingAddresses() {
		return sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class).list();
	}
	
	public MessagingAddress getMessagingAddress(Integer addressId) {
		return (MessagingAddress) sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class).add(Restrictions.eq("addressId", addressId)).uniqueResult();
	}
	
	public List<MessagingAddress> getMessagingAddressesForGateway(MessagingGateway gateway) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(gateway.getMessagingAddressClass());
		return c.list();
	}
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForClass(Class<? extends A> addressClass) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(addressClass);
		return c.list();
	}
	
	public List<MessagingAddress> getMessagingAddressesForTypeName(String typeName) {
		Class aClass = MessagingService.getInstance().getAddressClassForAddressTypeName(typeName);
		if(aClass != null){
			return getMessagingAddressesForClass(aClass);
		}
		return null;
		
	}
	
	public List<MessagingAddress> getMessagingAddressesForPerson(Person person) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("person", person));
		return c.list();
	}
	
	public List<MessagingAddress> getMessagingAddressesForPersonAndGateway(Person person, MessagingGateway gateway) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(gateway.getMessagingAddressClass());
		if(person !=null){
			c.add(Restrictions.eq("person", person));
		}
		return c.list();
	}
	
	public <A extends MessagingAddress> List<A> getMessagingAddressesForPersonAndClass(Person person, Class<? extends A> addressClass) {
		Criteria c= sessionFactory.getCurrentSession().createCriteria(addressClass);
		if(person !=null){
			c.add(Restrictions.eq("person", person));
		}
		return c.list();
	}
	
	public List<MessagingAddress> getMessagingAddressesForPersonAndTypeName(Person person, String typeName) {
		Class aClass = MessagingService.getInstance().getAddressClassForAddressTypeName(typeName);
		if(aClass != null){
			return getMessagingAddressesForPersonAndClass(person,aClass);
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> findMessagingAddresses(String search) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.like("address","%"+search+"%"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MessagingAddress> findMessagingAddresses(String search, MessagingGateway gateway) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(gateway.getMessagingAddressClass());
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
		if(address.getPreferred()){
			List<MessagingAddress> addresses = getMessagingAddressesForPerson(address.getPerson());
			for(MessagingAddress ad:addresses){
				ad.setPreferred(false);
				saveMessagingAddress(ad);
			}
		}
		sessionFactory.getCurrentSession().save(address);
	}

	public void unvoidMessagingAddress(MessagingAddress address) throws DAOException{
		address.setVoided(false);
		sessionFactory.getCurrentSession().saveOrUpdate(address);
	}

	@Override
	public Person getPersonForAddress(String address) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("address", address));
		MessagingAddress ma = (MessagingAddress) c.uniqueResult();
		if(ma != null){
			return ma.getPerson();
		}
		return null;
	}

	@Override
	public MessagingAddress getMessagingAddress(String address) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("address",address));
		return (MessagingAddress) c.uniqueResult();
	}

}
