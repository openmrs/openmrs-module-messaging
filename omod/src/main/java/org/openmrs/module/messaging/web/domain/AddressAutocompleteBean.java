package org.openmrs.module.messaging.web.domain;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.domain.MessagingAddress;

public class AddressAutocompleteBean {
	private String label;
	private String value;
	
	public AddressAutocompleteBean(MessagingAddress address){
		value="";
		if(address.getPerson() != null){
			value += "\""+address.getPerson().getPersonName().toString()+"\" ";
		}
		String protocolAbbreviation = Context.getService(MessagingService.class).getProtocolByClass(address.getProtocol()).getProtocolAbbreviation();
		value+="<"+protocolAbbreviation+":"+address.getAddress()+">";
		label = value;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressAutocompleteBean other = (AddressAutocompleteBean) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
