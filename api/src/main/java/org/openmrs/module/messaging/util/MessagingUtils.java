/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.messaging.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

import java.util.UUID;

public class MessagingUtils {

	private static Log log = LogFactory.getLog(MessagingUtils.class);

	public static void createPatientAttributes() {
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		PersonService personService = Context.getPersonService();
		if (personService.getPersonAttributeTypeByName(MessagingConstants.SEND_OMAIL_ALERTS_ATTR_NAME) == null) {
			PersonAttributeType sendOmailAlerts = new PersonAttributeType();
			sendOmailAlerts.setName(MessagingConstants.SEND_OMAIL_ALERTS_ATTR_NAME);
			sendOmailAlerts.setFormat("java.lang.Boolean");
			sendOmailAlerts.setDescription("Boolean signalling whether or not the messaging module should send out alerts when a user receives OMail messages");
			sendOmailAlerts.setSearchable(false);
			personService.savePersonAttributeType(sendOmailAlerts);
		}
		if (personService.getPersonAttributeTypeByName(MessagingConstants.ALERT_ADDRESS_ATTR_NAME) == null) {
			PersonAttributeType alertAddress = new PersonAttributeType();
			alertAddress.setName(MessagingConstants.ALERT_ADDRESS_ATTR_NAME);
			alertAddress.setFormat("java.lang.Integer");
			alertAddress.setDescription("Int id for the address to which we should send OMail alerts");
			alertAddress.setSearchable(false);
			personService.savePersonAttributeType(alertAddress);
		}
	}

	/**
	 * This method creates the task that polls the database and dispatches outgoing messages
	 */
	public static void createGatewayManagerTask() {
		//temporarily add the privilege to manage the scheduler
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		TaskDefinition dispatchMessagesTaskDef = Context.getSchedulerService().getTaskByName(MessagingConstants.TASK_NAME);

		if (dispatchMessagesTaskDef == null) {
			dispatchMessagesTaskDef = new TaskDefinition();
			dispatchMessagesTaskDef.setUuid(UUID.randomUUID().toString());
			dispatchMessagesTaskDef.setName(MessagingConstants.TASK_NAME);
			dispatchMessagesTaskDef.setDescription("Handles the dispatching of messages for the Messaging Module.");
			dispatchMessagesTaskDef.setStartOnStartup(true);
			dispatchMessagesTaskDef.setStartTime(null);
			dispatchMessagesTaskDef.setRepeatInterval(5L);
			dispatchMessagesTaskDef.setTaskClass("org.openmrs.module.messaging.schedulertask.DispatchMessagesTask");
			try {
				Context.getSchedulerService().scheduleTask(dispatchMessagesTaskDef);
			} catch (SchedulerException e) {
				log.error("Error creating the gateway manager task in the scheduler", e);
			}
		}
	}
}
