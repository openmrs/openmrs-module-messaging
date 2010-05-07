package org.openmrs.module.messaging.nuntium;

import org.openmrs.module.messaging.schema.Message;

public class NuntiumMessage extends Message {

	public NuntiumMessage(String destination, String content) {
		super(destination, content);
	}
	
}
