package org.openmrs.module.messaging.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.messaging.db.PersonAttributeDAO;

public class HibernatePersonAttributeDAO implements PersonAttributeDAO{

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
	
	public List<PersonAttribute> getPersonAttributes(Person person, PersonAttributeType type, boolean includeVoided) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(PersonAttribute.class);
		c.add(Restrictions.eq("person",person));
		c.add(Restrictions.eq("attributeType",type));
		if(!includeVoided){
			c.add(Restrictions.eq("voided", false));
		}
		return c.list();
	}

	public void savePersonAttribute(PersonAttribute attribute) {
		sessionFactory.getCurrentSession().saveOrUpdate(attribute);
	}

	public PersonAttribute getPersonAttribute(Person person, PersonAttributeType type) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(PersonAttribute.class);
		c.add(Restrictions.eq("person",person));
		c.add(Restrictions.eq("attributeType",type));
		c.add(Restrictions.eq("voided", false));
		c.addOrder(Order.desc("dateCreated"));
		c.setMaxResults(1);
		return (PersonAttribute) c.uniqueResult();
	}
}
