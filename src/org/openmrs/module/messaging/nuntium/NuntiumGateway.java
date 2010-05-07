package org.openmrs.module.messaging.nuntium;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.AddressFactory;
import org.openmrs.module.messaging.schema.MessageDelegate;
import org.openmrs.module.messaging.schema.MessageFactory;
import org.openmrs.module.messaging.schema.MessagingGateway;

public class NuntiumGateway extends MessagingGateway<NuntiumMessage, NuntiumAddress> {

	private String username;
	private String password;
	
	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public boolean canSend() {
		return username != null && username.length() > 0 &&
		       password != null && password.length() > 0;
	}

	@Override
	public boolean canSendFromUserAddresses() {
		return true;
	}

	@Override
	public AddressFactory<NuntiumAddress> getAddressFactory() {
		return new NuntiumAddressFactory();
	}

	@Override
	public NuntiumAddress getDefaultSenderAddress() {
		return new NuntiumAddress();
	}

	@Override
	public String getDescription() {
		return "Nuntium";
	}

	@Override
	public String getGatewayId() {
		return "nuntium";
	}

	@Override
	public MessageFactory<NuntiumMessage, NuntiumAddress> getMessageFactory() {
		return new NuntiumMessageFactory();
	}

	@Override
	public String getName() {
		return "Nuntium";
	}

	@Override
	public void sendMessage(String address, String content) throws Exception {
		String to = URLEncoder.encode(address, "utf-8");
		String body = URLEncoder.encode(content, "utf-8");
		URL sendAoUrl = new URL("https://nuntium.instedd.org/send_ao?to=" + to + "&body=" + body);
		HttpURLConnection conn = (HttpURLConnection) sendAoUrl.openConnection();
		try
		{
			String authToken = Base64.encodeBytes((username + ":" + password).getBytes());
			conn.setRequestProperty("Authorization", "Basic " + authToken);
			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK)
				throw new IOException(conn.getResponseMessage());
		}
		finally
		{
			conn.disconnect();
		}
	}

	@Override
	public void sendMessage(NuntiumMessage message) throws Exception {
		sendMessage(message.getDestination(), message.getContent());
	}

	@Override
	public void sendMessage(NuntiumMessage message, MessageDelegate delegate) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendMessageToAddresses(NuntiumMessage m, List<String> addresses,
			MessageDelegate delegate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessages(List<NuntiumMessage> messages, MessageDelegate delegate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startup() {
		username = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_NUNTIUM_USERNAME);
		password = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_NUNTIUM_PASSWORD);
	}

}
