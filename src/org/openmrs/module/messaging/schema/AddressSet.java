package org.openmrs.module.messaging.schema;

import java.util.ArrayList;
import java.util.List;


public class AddressSet{
	
	protected String typeName;
	protected List<String> addresses;
	
	public AddressSet(String name){
		this.typeName = name;
		addresses = new ArrayList<String>();
	}
	
	public String getTypeName(){
		return typeName;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public List<String> getAddresses() {
		return addresses;
	}
	
	public void addAddress(String address){
		addresses.add(address);
	}
	
}
