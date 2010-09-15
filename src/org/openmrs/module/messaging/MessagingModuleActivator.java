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
package org.openmrs.module.messaging;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.messaging.schema.GatewayManager;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class MessagingModuleActivator extends BaseModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());
	
	private static String TASK_NAME="Messaging Module Gateway Manager";
	
	public static GatewayManager manager;
	
	public void started() {
		log.info("Started Messaging Module");
		//create the singleton gateway manager
		//TODO: there should be a better way of doing this
		//This method is called twice.
		if(manager == null){
			manager = new GatewayManager();
			createGatewayManagerTask();
		}else{
			log.info("I SEE YOU TRYING TO CALL STARTED() TWICE!!");
		}
	}

	public void willStop() {
		log.info("Shutting down Messaging Module");
	}
	
	/**
	 * This method creates the task that polls the database and dispatches outgoing messages
	 */
	private void createGatewayManagerTask(){
		Context.openSession();
		//temporarily add the privelege to manage the scheduler
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		TaskDefinition dispatchMessagesTaskDef= Context.getSchedulerService().getTaskByName(TASK_NAME);
		if(dispatchMessagesTaskDef == null){
			dispatchMessagesTaskDef = new TaskDefinition();
			dispatchMessagesTaskDef.setUuid(UUID.randomUUID().toString());
			dispatchMessagesTaskDef.setName(TASK_NAME);
			dispatchMessagesTaskDef.setDescription("Handles the dispatching of messages for the Messaging Module.");
			dispatchMessagesTaskDef.setStartOnStartup(true);
			dispatchMessagesTaskDef.setStartTime(null);
			dispatchMessagesTaskDef.setRepeatInterval(2L);
			dispatchMessagesTaskDef.setTaskClass("org.openmrs.module.messaging.schema.DispatchMessagesTask");
			try {
				Context.getSchedulerService().scheduleTask(dispatchMessagesTaskDef);
			} catch (SchedulerException e) {
				log.error("Error creating the gateway manager task in the scheduler", e);
			}
		}
		Context.closeSession();
	}
	
	
}
