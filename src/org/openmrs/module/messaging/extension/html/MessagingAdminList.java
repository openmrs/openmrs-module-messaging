package org.openmrs.module.messaging.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.web.extension.AdministrationSectionExt;

public class MessagingAdminList extends AdministrationSectionExt {

	@Override
	public Map<String, String> getLinks() {
		Map<String,String> links = new HashMap<String,String>();
		links.put("module/messaging/xyz.form", "messaging.first.link");
		return links;
	}

	@Override
	public String getTitle() {
		return "messaging.title";
	}

}
