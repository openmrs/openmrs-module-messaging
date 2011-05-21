package org.openmrs.module.messaging;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

public interface PersonAttributeService extends OpenmrsService{
	
	@Transactional(readOnly=true)
	public List<PersonAttribute> getPersonAttributes(Person person, PersonAttributeType type, boolean includeVoided);
	
	@Transactional
	public void savePersonAttribute(PersonAttribute attribute);
}
