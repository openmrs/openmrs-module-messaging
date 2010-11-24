package org.openmrs.module.messaging.sms.servicemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DetectorUtils {
	
	private static Log log = LogFactory.getLog(DetectorUtils.class);
	
	/** Calls {@link Thread#sleep(long)} and ignores {@link InterruptedException}s thrown. */
	public static final void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException ex) {
			// ignore
		}
	}
	
	/**
	 * Read all bytes available on the input stream, and concatenates them into a string.
	 * N.B. This casts bytes read directly to characters.
	 */
	public static final String readAll(InputStream in) throws IOException {
		StringBuilder bob = new StringBuilder();
		int c;
		while((c = in.read()) != -1) {
			bob.append((char) c);
		}
		return bob.toString();
	}
	
	/** Writes the supplied command to the output stream, followed by a \r character */
	public static final void writeCommand(OutputStream out, String command) throws IOException {
		for(char c : command.toCharArray()) out.write(c);
		out.write('\r');
	}
	
	/** @return <code>true</code> if the response contains "OK" */
	public static final boolean isResponseOk(String response) {
		return response.indexOf("OK") != -1;
	}
	
	/** @return the response with the original command, "OK" and all trailing and leading whitespace removed */
	public static final String trimResponse(String command, String response) {
		String minicommand = command.replace("AT", "");
		return response.replace("OK", "").replace(command, "").replace(minicommand, "").replace("\"", "").trim();
	}
	
	public static String parseCNUM(String raw){
		raw = raw.substring(0,raw.lastIndexOf("\""));
		raw = raw.substring(raw.lastIndexOf("\""));
		return raw;
	}
}