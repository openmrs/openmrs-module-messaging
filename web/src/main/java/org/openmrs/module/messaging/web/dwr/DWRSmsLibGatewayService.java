package org.openmrs.module.messaging.web.dwr;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.Modem;
import org.openmrs.module.messaging.sms.SmsLibGateway;
import org.openmrs.module.messaging.sms.servicemanager.exception.ServiceStateException;

public class DWRSmsLibGatewayService {

	public List<Modem> redetectModems() throws ServiceStateException{
		SmsLibGateway gateway = Context.getService(MessagingService.class).getGatewayManager().getGatewayByClass(SmsLibGateway.class);
		return gateway.redetectModems();
	}
	
	public List<Modem> getConnectedModems(){
		SmsLibGateway gateway = Context.getService(MessagingService.class).getGatewayManager().getGatewayByClass(SmsLibGateway.class);
		return gateway.getCurrentlyConnectedModems(true);
	}
}
