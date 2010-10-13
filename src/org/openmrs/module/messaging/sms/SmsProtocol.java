package org.openmrs.module.messaging.sms;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.Message;
import org.openmrs.module.messaging.schema.MessagingAddress;
import org.openmrs.module.messaging.schema.Protocol;
import org.openmrs.module.messaging.schema.exception.AddressFormattingException;
import org.openmrs.module.messaging.schema.exception.MessageFormattingException;

/**
 * A protocol for sending SMS messages
 * @author dieterichlawson
 *
 */
public class SmsProtocol extends Protocol{

	public static String PROTOCOL_ID = "sms";
	/**
	 * SmsAlphabet contains data about the 
	 * 3 different possible alphabets that can
	 * be used with SMS.
	 */
	public enum SmsAlphabet{
		GSM_7_BIT(160,153),
		GSM_8_BIT(140,134),
		UTF_16(70,67);
		
		private int maxSingleLength;
		private int maxConcatenatedLength;
		
		private SmsAlphabet(int singleLength, int concatLength){
			this.maxSingleLength = singleLength;
			this.maxConcatenatedLength = concatLength;
		}

		/**
		 * @return the maxSingleLength
		 */
		public int getMaxSingleLength() {
			return maxSingleLength;
		}
		
		/**
		 * @return the maxConcatenatedLength
		 */
		public int getMaxConcatenatedLength() {
			return maxConcatenatedLength;
		}
	}
	
	@Override
	public String getProtocolId() {
		return PROTOCOL_ID;
	}
	
	@Override
	public String getProtocolName() {
		return "SMS";
	}

	/** 
	 * Validating phone numbers is hard to do since different nations use
	 * different numbering systems. This method does the bare minimum of
	 * stripping all non-numeric characters from the string and checking if
	 * it is at least 10 characters.
	 * 
	 * @see org.openmrs.module.messaging.schema.Protocol#addressIsValid(java.lang.String)
	 */
	@Override
	public boolean addressIsValid(String address) {
		address = address.replaceAll("[^0-9]", "");
		return address.length() >= 10;
	}

	@Override
	public MessagingAddress createAddress(String address, Person person) throws AddressFormattingException {
		if (!addressIsValid(address)) {
			throw new AddressFormattingException("The phone number you entered was not valid. Either enter a number prefixed with a + "
											   + "and your country code or enter a locally formatted number.");
		}
		MessagingAddress result = new MessagingAddress(getProperlyFormattedPhoneNumber(address), person);
		result.setProtocolId(this.PROTOCOL_ID);
		return result;
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
	 * Creates an SMS message. Validates message content using the default
	 * assumptions (7-bit GSM alphabet, max 3 concatenated SMS). If null safe for 'fromAddress',
	 * the only parameter that is allowed to be null
	 * 
	 * @see org.openmrs.module.messaging.schema.Protocol#createMessage(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Message createMessage(String messageContent, String toAddress, String fromAddress) throws MessageFormattingException, AddressFormattingException {
		try{
			MessagingAddress to = createAddress(toAddress,null);
		}catch(AddressFormattingException e){
			AddressFormattingException f = new AddressFormattingException(e.getMessage().replace("phone number", "\"to\" number"));
			throw f;
		}
		if (fromAddress != null && !fromAddress.equals("")) {
			try {
				MessagingAddress from = createAddress(fromAddress, null);
			} catch (AddressFormattingException e) {
				AddressFormattingException f = new AddressFormattingException(e.getMessage().replace("phone number", "\"from\" number"));
				throw f;
			}
		}
		if(!messageContentIsValid(messageContent)){
			throw new MessageFormattingException("SMS message is too long.");
		}
		Message result = new Message(toAddress,fromAddress,messageContent);
		result.setProtocolId(this.PROTOCOL_ID);
		return result; 
	}
	
	/**
	 * Validates the message content using the supplied paramaters instead of the defaults
	 * @throws MessageFormattingException
	 */
	public Message createMessage(String messageContent, MessagingAddress toAddress, MessagingAddress fromAddress, int maxSmsNumber, SmsAlphabet alphabet) throws MessageFormattingException {
		if(!messageContentIsValid(messageContent,maxSmsNumber,alphabet)){
			throw new MessageFormattingException("SMS message is too long.");
		}
		//validation complete, create the message
		Message result =new Message(toAddress.getAddress(),fromAddress.getAddress(),messageContent);
		result.setProtocolId(this.PROTOCOL_ID);
		return result;
	}
	

	/**
	 * Checking for SMS content validity is rather complicated, so this method
	 * assumes that you are using the GSM 7-bit alphabet and that the max number
	 * of concatenated SMS messages is 3. If you need more control over the
	 * parameters, use this method:
	 * {@link #messageContentIsValid(String, int, SmsAlphabet)}
	 * 
	 * @see org.openmrs.module.messaging.schema.Protocol#messageContentIsValid(java.lang.String)
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
			return content.length() <= alphabet.getMaxSingleLength();
		}else{// concatenated SMS
			return content.length() <= alphabet.getMaxConcatenatedLength() * maxNumberOfSms;
		}
	}

	@Override
	public boolean requiresPassword() {
		return false;
	}

}
