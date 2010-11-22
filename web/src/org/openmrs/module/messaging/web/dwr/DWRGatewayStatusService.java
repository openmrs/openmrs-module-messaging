package org.openmrs.module.messaging.web.dwr;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;

public class DWRGatewayStatusService {
	
	public boolean isGatewayActive(String gatewayId) throws Exception{
		Class<? extends MessagingGateway> gatewayClass = getClassFromString(gatewayId);
		return Context.getService(MessagingService.class).getGatewayManager().getGatewayByClass(gatewayClass).isActive();
	}
	
	public void startGateway(String gatewayId) throws Exception{
		Class<? extends MessagingGateway> gatewayClass = getClassFromString(gatewayId);
		Context.getService(MessagingService.class).getGatewayManager().getGatewayByClass(gatewayClass).startup();
	}
	
	public void stopGateway(String gatewayId) throws Exception{
		Class<? extends MessagingGateway> gatewayClass = getClassFromString(gatewayId);
		Context.getService(MessagingService.class).getGatewayManager().getGatewayByClass(gatewayClass).shutdown();
	}
	
	private Class<? extends MessagingGateway> getClassFromString(String className) throws Exception{
		Class<? extends MessagingGateway> gatewayClass;
		try {
			gatewayClass = (Class<? extends MessagingGateway>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new Exception("There is no gateway with that ID");
		}
		return gatewayClass;
	}
}
