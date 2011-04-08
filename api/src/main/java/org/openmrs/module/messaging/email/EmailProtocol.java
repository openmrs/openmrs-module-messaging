package org.openmrs.module.messaging.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.springframework.util.StringUtils;

/**
 * Email protocol for use with Messaging Module
 */
public class EmailProtocol extends Protocol {

	/**
	 * pattern for use with validating email addresses
	 */
	static final Pattern emailPattern = Pattern.compile(
			"^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * logging facility
	 */
	private static Log log = LogFactory.getLog(EmailProtocol.class);
	
	/**
	 * provide protocol's name
	 */
	@Override
	public String getProtocolName() {
		return "Email";
	}
	
	/**
	 * only return false if the content is null or otherwise empty
	 */
	@Override
	public boolean messageContentIsValid(String content) {
		return StringUtils.hasText(content);
	}

	/**
	 * determine if an address is valid
	 */
	@Override
	public boolean addressIsValid(String address) {
		if (!StringUtils.hasText(address))
			return false;
		Matcher matcher = emailPattern.matcher(address);
		return matcher.matches();
	}

	@Override
	public String getProtocolAbbreviation() {
		return "email";
	}
}
