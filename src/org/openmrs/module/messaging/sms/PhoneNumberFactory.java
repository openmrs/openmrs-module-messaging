package org.openmrs.module.messaging.sms;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingConstants;
import org.openmrs.module.messaging.schema.AddressFormattingException;
import org.openmrs.module.messaging.schema.BaseAddressFactory;

public class PhoneNumberFactory extends BaseAddressFactory<PhoneNumber> {
	
	public PhoneNumberFactory(){}
	
	/**
	 * Validating phone numbers is hard to do since different nations use
	 * different numbering systems. This method does the bare minimum of
	 * stripping all non-numeric characters from the string and adding the
	 * default country code if it isn't already there.
	 * 
	 * @see org.openmrs.module.messaging.schema.AddressFactory#addressIsValid(java.lang.String)
	 */
	public Boolean addressIsValid(String address) {
		address = address.replaceAll("[^0-9]", "");
		return address.length() >= 10;
	}

	public PhoneNumber createAddress(String address, Person person) throws AddressFormattingException {
		if (!addressIsValid(address)) {
			throw new AddressFormattingException("The phone number you entered was not valid. Either enter a number prefixed with a + "
							+ "and your country code or enter a locally formatted number.");
		}
		return new PhoneNumber(getProperlyFormattedPhoneNumber(address), person);
	}

	public String getFormatHint(String currentAddress) {
		return "";
	}

	/**
	 * precondition: number is determined to be valid
	 * 
	 * @param rawNumber
	 * @return
	 */
	public String getProperlyFormattedPhoneNumber(String rawNumber) {
		String defaultCountryCode = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_DEFAULT_COUNTRY_CODE);
		rawNumber = rawNumber.replaceAll("[^0-9+]", "");
		if (!rawNumber.startsWith("+")) {
			rawNumber = "+" + defaultCountryCode + rawNumber;
		}else if(rawNumber.startsWith(defaultCountryCode) && rawNumber.length() == (defaultCountryCode.length() + 10)){
			rawNumber = "+"+rawNumber;
		}
		return rawNumber;
	}

}
