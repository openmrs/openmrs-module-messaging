package org.openmrs.module.messaging.extension.html;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
		HashMap<String, String> links = new LinkedHashMap<String,String>();
		links.put("module/messaging/inbox.form", "Inbox");
		links.put("module/messaging/compose_message.form", "Compose Message");
		links.put("module/messaging/sent_messages.form", "Sent Messages");
		links.put("module/messaging/external_conversations.form", "External Conversations");
		links.put("module/messaging/settings.form", "Messaging Settings");
		links.put("module/messaging/manage_gateways.form", "Manage Messaging Gateways");
		return links;
	}

	@Override
	public String getTitle() {
		return "Messaging";
	}

}
