package org.openmrs.module.messaging.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class AdminList extends AdministrationSectionExt{

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public Map<String, String> getLinks() {
		HashMap<String, String> links = new HashMap<String,String>();
		links.put("module/messaging/sendMessage.form", "Send a Message");
		return links;
	}

	@Override
	public String getTitle() {
		return "Messaging";
	}

}
