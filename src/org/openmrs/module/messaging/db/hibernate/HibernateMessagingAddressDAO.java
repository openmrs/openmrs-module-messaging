package org.openmrs.module.messaging.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.messaging.db.MessagingAddressDAO;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.Protocol;

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
		return (MessagingAddress) sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class).add(Restrictions.eq("messagingAddressId", addressId)).uniqueResult();
	}

	public MessagingAddress getPreferredMessagingAddressForPerson(Person person) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public List<MessagingAddress> findMessagingAddresses(String address, Protocol protocol, Person person) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		if(address != null && !address.equals("")){
			c.add(Restrictions.like("address", address,MatchMode.ANYWHERE));
		}
		if(protocol != null){
			c.add(Restrictions.eq("protocolId", protocol.getProtocolId()));
		}
		if(person != null){
			c.add(Restrictions.eq("person", person));
		}
		return c.list();
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
			List<MessagingAddress> addresses = findMessagingAddresses(null,null,address.getPerson());
			for(MessagingAddress ad:addresses){
				if(!ad.equals(address)){
					ad.setPreferred(false);
					saveMessagingAddress(ad);
				}
			}
		}
		sessionFactory.getCurrentSession().saveOrUpdate(address);
	}

	public void unvoidMessagingAddress(MessagingAddress address) throws DAOException{
		address.setVoided(false);
		sessionFactory.getCurrentSession().saveOrUpdate(address);
	}

	public Person getPersonForAddress(String address) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("address", address));
		MessagingAddress ma = (MessagingAddress) c.uniqueResult();
		if(ma != null){
			return ma.getPerson();
		}
		return null;
	}

	public MessagingAddress getMessagingAddress(String address) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MessagingAddress.class);
		c.add(Restrictions.eq("address",address));
		return (MessagingAddress) c.uniqueResult();
	}

}
