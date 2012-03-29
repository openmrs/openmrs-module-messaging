package org.openmrs.module.messaging.web.dwr;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.PersonAttributeService;

public class DWRMessagingSettingsService {
	Log log = LogFactory.getLog(getClass());
	
	public DWRMessagingSettingsService(){}
	
	public void setAlertSettings(Boolean shouldAlert, Integer messagingAddressId){
		log.info("Setting Omail Alert settings.");

		//Reload the Person object to avoid LazyInitializationException
        Person per = Context.getPersonService().getPerson(Context.getAuthenticatedUser().getPerson().getPersonId());

        //we should return if they are trying to alert a null address
		if(shouldAlert && (messagingAddressId == null || messagingAddressId <= 0)) return;
		//void the old attributes
		PersonAttributeService personAttrService = Context.getService(PersonAttributeService.class);
		PersonAttributeType shouldAlertType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.SEND_OMAIL_ALERTS_ATTR_NAME);
		PersonAttributeType alertAddressType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.ALERT_ADDRESS_ATTR_NAME);
		List<PersonAttribute> attributes = personAttrService.getPersonAttributes(per, shouldAlertType, false);
		for(PersonAttribute attr: attributes){
			attr.voidAttribute("New data provided");
			personAttrService.savePersonAttribute(attr);
		}
		attributes = personAttrService.getPersonAttributes(per, alertAddressType, false);
		for(PersonAttribute attr: attributes){
			attr.voidAttribute("New data provided");
			personAttrService.savePersonAttribute(attr);
		}
		
		//create the new ones
		PersonAttribute shouldAlertAttr = new PersonAttribute(shouldAlertType,shouldAlert.toString());
		Set<PersonAttribute> attrSet = per.getAttributes();
		if(attrSet == null) {
		    attrSet = new TreeSet<PersonAttribute>();
		    per.setAttributes(attrSet);
		}
		attrSet.add(shouldAlertAttr);
		if(messagingAddressId != null && messagingAddressId != 0 && shouldAlert){
			PersonAttribute alertAddressAttr = new PersonAttribute(alertAddressType,messagingAddressId.toString());
			attrSet.add(alertAddressAttr);
		}
		//per.setAttributes(attrSet);
		
		//save the attributes 
		Context.getPersonService().savePerson(per);
	}
}
