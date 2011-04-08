package org.openmrs.module.messaging.sms;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.util.MessagingConstants;

/**
 * A protocol for sending SMS messages
 * @author dieterichlawson
 *
 */
public class SmsProtocol extends Protocol{
	
	@Override
	public String getProtocolName() {
		return "SMS";
	}
	
	/**
	 * Takes in a user-entered number and spits out a properly formatted one, with 
	 * a country code and preceding '+'. If the number already starts with a '+',
	 * then it assumes that the user included the proper country code.
	 * 
	 * Precondition: number is valid
	 * @param rawNumber
	 * @return
	 */
	public String getProperlyFormattedPhoneNumber(String rawNumber) {
		//get the default country code
		String defaultCountryCode = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_COUNTRY_CODE);
		//strip all non-numeric (and '+') characters
		rawNumber = rawNumber.replaceAll("[^0-9+]", "");
		//if the number does not start with a '+', prepend the default country code
		if (!rawNumber.startsWith("+")) {
			rawNumber = "+" + defaultCountryCode + rawNumber;
		}
		return rawNumber;
	}
	
	/** 
	 * Validating phone numbers is hard to do since different nations use
	 * different numbering systems. This method does the bare minimum of
	 * stripping all non-numeric characters from the string and checking if
	 * it is at least 10 characters.
	 * 
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#addressIsValid(java.lang.String)
	 */
	@Override
	public boolean addressIsValid(String address) {
		address = address.replaceAll("[^0-9]", "");
		return address.length() >= 10;
	}

	/**
	 * Checking for SMS content validity is rather complex, so this method
	 * assumes that you are using the GSM 7-bit alphabet and that the max number
	 * of concatenated SMS messages is 3. If you need more control over the
	 * parameters, use this method:
	 * {@link #messageContentIsValid(String, int, SmsAlphabet)}
	 * 
	 * @see org.openmrs.module.messaging.domain.gateway.Protocol#messageContentIsValid(java.lang.String)
	 */
	@Override
	public boolean messageContentIsValid(String content) {
		return messageContentIsValid(content,3,SmsAlphabet.GSM_7_BIT);
	}
	
	/**
	 * Returns whether or not the message content is valid.
	 * Currently only checks the length of the message, but *should*
	 * eventually check for any non-allowed characters.
	 */
	public boolean messageContentIsValid(String content, int maxNumberOfSms, SmsAlphabet alphabet){
		if(maxNumberOfSms < 1){//this is invalid
			return false;
		}else if(maxNumberOfSms ==1){// single SMS, so use single length
			return content.length() <= alphabet.maxSingleLength;
		}else{// concatenated SMS
			return content.length() <= alphabet.maxConcatenatedLength * maxNumberOfSms;
		}
	}
	
	/**
	 * SmsAlphabet contains data about the 
	 * 3 different possible alphabets that can
	 * be used with SMS.
	 */
	public enum SmsAlphabet{
		GSM_7_BIT(160,153),
		GSM_8_BIT(140,134),
		UTF_16(70,67);
		
		public final int maxSingleLength;
		public final int maxConcatenatedLength;
		
		private SmsAlphabet(int singleLength, int concatLength){
			this.maxSingleLength = singleLength;
			this.maxConcatenatedLength = concatLength;
		}
	}

	@Override
	public String getProtocolAbbreviation() {
		return "sms";
	}
}
