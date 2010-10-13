package org.openmrs.module.messaging.nuntium;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.openmrs.module.messaging.schema.Message;

import sun.misc.BASE64Encoder;

public class Nuntium {
	
	public static enum CredentialsCheckResult {
		Ok,
		UnknownHost,
		InvalidCredentials,
	}
	
	private final String url;
	private final String account;
	private final String application;
	private final String password;

	public Nuntium(String url, String account, String application, String password) {
		if (url.endsWith("/"))
			url = url.substring(0, url.length() - 1);
		
		this.url = url;
		this.account = account;
		this.application = application;
		this.password = password;
	}

	public CredentialsCheckResult checkCredentials() {
		try {
			URL u = new URL(url + "/api/channels.json");
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			setCredentials(conn);
			InputStream in = conn.getInputStream();
			in.close();
			return CredentialsCheckResult.Ok;
		} catch (UnknownHostException e) {
			return CredentialsCheckResult.UnknownHost;
		} catch (Exception e) {
			return CredentialsCheckResult.InvalidCredentials;	
		}
	}

	public void sendMessage(Message message) throws Exception {
		URL u = new URL(url + "/" + account + "/" + application + "/send_ao");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		setCredentials(conn);
		OutputStream out = conn.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		
		writer.write("to=");
		writer.write(encode(toNuntiumAddress(message.getProtocolId(), message.getDestination())));
		
		writer.write("&");
		writer.write("body=");
		writer.write(encode(message.getContent()));
		writer.close();
		out.close();
		
		conn.getInputStream().close();
	}
	
	private String toNuntiumAddress(String protocolId, String address) {
		if (address.startsWith("+"))
			address = address.substring(1);
		return protocolId + "://" + address;
	}
	
	private void setCredentials(HttpURLConnection conn) {
		conn.setRequestProperty("Authorization", "Basic " + new BASE64Encoder().encode((account + "/" + application + ":" + password).getBytes()));
	}
	
	private String encode(String s) throws Exception {
		return URLEncoder.encode(s, "UTF-8");
	}

}
