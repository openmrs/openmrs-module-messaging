package org.openmrs.module.messaging.schema;

public enum MessageStatus{
		SENT(),
		RECEIVED(),
		RETRYING,
		FAILED(),
		OUTBOX();
}