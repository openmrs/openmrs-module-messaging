package org.openmrs.module.messaging.web.dwr;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		//void the old attributes
		PersonAttributeService personAttrService = Context.getService(PersonAttributeService.class);
		PersonAttributeType shouldAlertType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.SEND_OMAIL_ALERTS_ATTR_NAME);
		PersonAttributeType alertAddressType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.ALERT_ADDRESS_ATTR_NAME);
		List<PersonAttribute> attributes = personAttrService.getPersonAttributes(Context.getAuthenticatedUser().getPerson(), shouldAlertType, false);
		for(PersonAttribute attr: attributes){
			attr.voidAttribute("New data provided");
			personAttrService.savePersonAttribute(attr);
		}
		attributes = personAttrService.getPersonAttributes(Context.getAuthenticatedUser().getPerson(), alertAddressType, false);
		for(PersonAttribute attr: attributes){
			attr.voidAttribute("New data provided");
			personAttrService.savePersonAttribute(attr);
		}
		
		//create the new ones
		PersonAttribute shouldAlertAttr = new PersonAttribute(shouldAlertType,shouldAlert.toString());
		PersonAttribute alertAddressAttr = new PersonAttribute(alertAddressType,messagingAddressId.toString());
		SortedSet<PersonAttribute> attrSet = new TreeSet<PersonAttribute>();
		attrSet.add(shouldAlertAttr);
		attrSet.add(alertAddressAttr);
		Context.getAuthenticatedUser().getPerson().setAttributes(attrSet);
		//save the attributes
		Context.getPersonService().savePerson(Context.getAuthenticatedUser().getPerson());
	}
}
