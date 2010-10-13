package org.openmrs.module.messaging.nuntium;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.nuntium.Nuntium.CredentialsCheckResult;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingGateway;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.sms.SmsProtocol;
import org.openmrs.module.messaging.twitter.TwitterProtocol;

public class NuntiumGateway extends MessagingGateway {
	
	private Nuntium nuntium;
	
	public void setNuntium(Nuntium nuntium) {
		this.nuntium = nuntium;
	}
	
	public Nuntium getNuntium() {
		if (nuntium == null) {
			AdministrationService admin = Context.getAdministrationService();
			nuntium = new Nuntium(
					admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_URL),
					admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ACCOUNT),
					admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_APPLICATION),
					admin.getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_PASSWORD)
					);
		}
		return nuntium;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canSend() {
		return true;
	}

	@Override
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("messaging.nuntium.gateway.description");
	}

	@Override
	public String getName() {
		return "Nuntium";
	}

	@Override
	public boolean isActive() {
		String enabled = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_NUNTIUM_ENABLED, "no");
		if ("yes".equals(enabled)) {
			return getNuntium().checkCredentials() == CredentialsCheckResult.Ok;
		} else {
			return false;
		}
	}

	@Override
	public void sendMessage(Message message) throws Exception {
		getNuntium().sendMessage(message);
	}

	@Override
	public boolean shouldSendMessage(Message m) {
		return supportsProtocol(m.getProtocolId());
	}

	@Override
	public void shutdown() {
		// Nothing to do
	}

	@Override
	public void startup() {
		// Nothing to do
	}

	@Override
	public boolean supportsProtocol(Protocol p) {
		return supportsProtocol(p.getProtocolId());
	}
	
	private boolean supportsProtocol(String protocolId) {
		return TwitterProtocol.PROTOCOL_ID.equals(protocolId) ||
			SmsProtocol.PROTOCOL_ID.equals(protocolId);
	}

}
