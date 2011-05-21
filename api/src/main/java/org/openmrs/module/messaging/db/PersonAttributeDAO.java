package org.openmrs.module.messaging.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

public interface PersonAttributeDAO {

	public List<PersonAttribute> getPersonAttributes(Person person, PersonAttributeType type, boolean includeVoided);

	public void savePersonAttribute(PersonAttribute attribute);

}
