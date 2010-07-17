package org.openmrs.module.messaging.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.MessagingService;

public class MessagingAddressTypeTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private MessagingAddress address = null;
	private Integer addressId = null;
	
	public int doStartTag(){
		if(addressId != null){
			address = Context.getService(MessagingAddressService.class).getMessagingAddress(addressId);
		}
		if(address !=null){
//			try {
//				pageContext.getOut().write(MessagingService.getInstance().getAddressFactoryForAddressClass(address.getClass()).getAddressTypeName());
//			} catch (IOException e) {
//				log.error("Unable to write address type to output", e);
//			}
		}
		reset();
		return SKIP_BODY;
	}
	
	
	public void reset(){
		setAddress(null);
		setAddressId(null);
	}


	public void setAddress(MessagingAddress address) {
		this.address = address;
	}


	public MessagingAddress getAddress() {
		return address;
	}


	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}


	public Integer getAddressId() {
		return addressId;
	}

}
