package org.openmrs.module.messaging.impl;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.messaging.PersonAttributeService;
import org.openmrs.module.messaging.db.PersonAttributeDAO;

public class PersonAttributeServiceImpl extends BaseOpenmrsService implements PersonAttributeService {

	protected PersonAttributeDAO dao;
	
	public void setPersonAttributeDAO(PersonAttributeDAO dao) {
		this.dao = dao;
	}	
	
	public List<PersonAttribute> getPersonAttributes(Person person, PersonAttributeType type, boolean includeVoided) {	
		return dao.getPersonAttributes(person,type,includeVoided);
	}

	public void savePersonAttribute(PersonAttribute attribute) {
		dao.savePersonAttribute(attribute);
	}
}
