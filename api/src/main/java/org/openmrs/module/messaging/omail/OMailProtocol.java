package org.openmrs.module.messaging.omail;

import org.openmrs.module.messaging.domain.gateway.Protocol;

public class OMailProtocol extends Protocol {
	
	@Override
	public String getProtocolAbbreviation() {
		return "omail";
	}

	@Override
	public String getProtocolName() {
		return "OMail";
	}

	/**
	 * With OMail, the message content is always valid
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean messageContentIsValid(String content) {
		return true;
	}
	
	/**
	 * Because OMail addreses depend on the person, not the text of the address, the address is always valid
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean addressIsValid(String address) {
		return true;
	}
}