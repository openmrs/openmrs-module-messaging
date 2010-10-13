package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.messaging.googlevoice.GoogleVoiceGateway;
import org.openmrs.module.messaging.nuntium.NuntiumGateway;
import org.openmrs.module.messaging.sms.SmsLibGateway;
import org.openmrs.module.messaging.twitter.TwitterGateway;

public class GatewayManager {

	/** All the messaging gateways that this manager manages*/
	private Map<Class<? extends MessagingGateway>,MessagingGateway> gateways;
	
	private static Log log = LogFactory.getLog(GatewayManager.class);
	
	public GatewayManager(){
		gateways = new HashMap<Class<? extends MessagingGateway>,MessagingGateway>();
		//add the gateways
		gateways.put(SmsLibGateway.class, new SmsLibGateway());
		gateways.put(TwitterGateway.class, new TwitterGateway());
		gateways.put(GoogleVoiceGateway.class, new GoogleVoiceGateway());
		gateways.put(NuntiumGateway.class, new NuntiumGateway());
		startGateways();
	}
	
	/**
	 * Starts all the gateways
	 */
	private void startGateways(){
		for(MessagingGateway mg: gateways.values()){
			mg.startup();
			log.info("Gateway:\""+mg.getName()+"\" started up");
		}
	}
	
	private void stopGateways(){
		for(MessagingGateway mg: gateways.values()){
			mg.shutdown();
			log.info("Gateway:\""+mg.getName()+"\" was shut down");
		}
	}
	
	/**
	 * Returns all gateways that are active and support sending
	 * messages encoding in the supplied protocol
	 */
	public List<MessagingGateway> getActiveSupportingGateways(Protocol p){
		List<MessagingGateway> results = new ArrayList<MessagingGateway>();
		for(MessagingGateway mg: gateways.values()){
			if(mg.canSend() && mg.isActive() && mg.supportsProtocol(p)){
				results.add(mg);
			}
		}
		return results;
	}
	
	/**
	 * Returns all gateways that support sending
	 * messages encoding in the supplied protocol
	 */
	public List<MessagingGateway> getSupportingGateways(Protocol p){
		List<MessagingGateway> results = new ArrayList<MessagingGateway>();
		for(MessagingGateway mg: gateways.values()){
			if(mg.supportsProtocol(p)){
				results.add(mg);
			}
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public <G extends MessagingGateway> G getGatewayByClass(Class<? extends G> gatewayClass){
		return (G) gateways.get(gatewayClass);
	}
	
}
