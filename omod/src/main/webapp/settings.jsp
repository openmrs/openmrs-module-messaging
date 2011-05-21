<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<link rel="stylesheet" href="<openmrs:contextPath/>/moduleResources/messaging/css/settings.css" type="text/css"/>
<table id="index">
<tr>
	<td id="link-cell">
		<div id="link-panel">
			<a id="inbox-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/inbox.form">OMail Inbox</a>
			<a id="compose-message-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/compose_message.form">Compose Message</a>
			<a id="sent-messages-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/sent_messages.form">Sent Messages</a>
			<a id="settings-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/settings.form">Settings</a>
		</div>
	</td>
	<td id="settings-panel">
			<div id="address-settings">
				<span>Addresses</span>
				<div id="address-table-panel">
					<table id="address-table">
						<thead>
							<tr>
								<th>Address</th>
								<th>Type</th>
								<th>Preferred</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody id="address-table-body"></tbody>
					</table>
					<div id="edit-address-panel">
						<input type="text" id="address"></input>
						<select id="protocolClass">
							<option value="org.openmrs.module.messaging.sms.SmsProtocol">SMS</option>
							<option value="org.openmrs.module.messaging.email.EmailProtocol">Email</option>
						</select>
						<input type="checkbox" id="preferred">Preferred</input>
						<span id="messagingAddressId" style="display:none;">-1</span>
					</div>
					<div id="address-button-panel">
						<button id="add-address-button">Add Address</button>
						<button id="save-address-button">Save</button>
						<button id="cancel-address-button">Cancel</button><br/>
					</div>
				</div>
			</div>
			<div id="alert-settings">
				<hr/>
				<span style="display:block;">Alerts</span>
				<input type="checkbox" id="enable-alerts-checkbox" style="display:inline;"></input>
				<label for="enable-alerts-checkbox" style="display:inline;">Alert me when I have new OMail at</label>
				<select style="display:inline;" id="alert-address-select" disabled="true">
				</select><br/>
			</div>
</td>
</tr>
</table>

<script src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery-1.4.4.min.js"></script>
<script src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery.watermark.min.js"></script>
<openmrs:htmlInclude file="/dwr/engine.js"/>
<openmrs:htmlInclude file="/dwr/util.js"/>
<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingAddressService.js"></script>
<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingSettingsService.js"></script>
<script type="text/javascript">
	window.onload = init;
	var viewed = -1;
	var addressCache = { };
	var protocolNames= {"org.openmrs.module.messaging.sms.SmsProtocol":"SMS",
						"org.openmrs.module.messaging.omail.OMailProtocol":"OMail",
						"org.openmrs.module.messaging.email.EmailProtocol":"Email"};
	
	function init(){
		//add the "Add Address" event listener
		$j('#add-address-button').live("click",addAddressClick);
		$j('#save-address-button').live('click',saveAddressClick);
		$j('#cancel-address-button').live('click',cancelAddressClick);
		$j('#enable-alerts-checkbox').live('click',toggleAlerts);
		$j('#alert-address-select').live('change',alertsChanged);
		//watermark the address button
		//$j('#address-textbox').watermark('Address');
		fillAddressTable();
		fillAlertAddressSelect();
	}

	function fillAddressTable(){
		DWRMessagingAddressService.getAllAddressesForCurrentUser(function(addresses){
			dwr.util.removeAllRows("address-table-body", { filter:function(tr) {return (tr.id != "addressRow");}});
			var address;
			// iterate through the messages, cloning the pattern row
			// and placing each message values into that row
			for (var i = 0; i < addresses.length; i++) {
				address = addresses[i];
				if(protocolNames[address.protocolClass] == "OMail") continue;
				$j(cloneAddressRow(address.id)).appendTo("#address-table-body");
			    $j("#address-row-address" + address.id).html(address.address);
			    $j("#address-row-type" + address.id).html(protocolNames[address.protocolClass]);
			    $j("#address-row-preferred" + address.id).html(address.preferred?"*":"");
				$j("#edit-link"+address.id).attr("href","#");
				$j("#delete-link"+address.id).attr("href","#");
				addressCache[address.id] = address;
			}
		});
	}

	function fillAlertAddressSelect(){
		DWRMessagingAddressService.getAllAddressesForCurrentUser(function(addresses) {
			dwr.util.removeAllOptions("alert-address-select");
			dwr.util.addOptions("alert-address-select",addresses,"messagingAddressId","address");
		});
	}
 
	function addAddressClick(event){
		toggleEditingAddress();
	}
	
	function cancelAddressClick(event){
		toggleEditingAddress();
		clearEditingArea();
	}
	
	function saveAddressClick(event){
		writeAddress();
	}
	
	function editClicked(eleid) {
		// we were an id of the form "edit{id}", eg "edit42". We lookup the "42"
		var address = addressCache[eleid.substring(9)];
		// put the address's values into the editing area
		dwr.util.setValues(address);
		if ($j("#edit-address-panel").is(":hidden")){
			toggleEditingAddress();
		}
	}
	
	function deleteClicked(eleid) {
		// we were an id of the form "delete{id}", eg "delete42". We lookup the "42"
		var address = addressCache[eleid.substring(11)];
		//confirm the delete
		if (confirm("Are you sure you want to delete address " + address.address + "?")) {
	    	dwr.engine.beginBatch();
	    	DWRMessagingAddressService.deleteAddress(address.messagingAddressId);
	    	fillAddressTable();
	    	fillAlertAddressSelect();
	    	dwr.engine.endBatch();
	    	clearEditingArea();
			if ($j("#edit-address-panel").is(":hidden")==false){
				toggleEditingAddress();
			}
	  	}
	}

	function clearEditingArea() {
	 	viewed = -1;
		dwr.util.setValues({ messagingAddressId:-1, address:null, preferred:null });
	}

	function writeAddress() {
		  var address = { messagingAddressId:viewed, protocolClass:null, address:null,preferred:null};
		  dwr.util.getValues(address);
		  dwr.engine.beginBatch();
		  DWRMessagingAddressService.saveOrUpdateAddressForCurrentUser(address);
		  fillAddressTable();
		  fillAlertAddressSelect();
		  dwr.engine.endBatch();
		  clearEditingArea();
		  toggleEditingAddress();
	}
	
	function toggleEditingAddress(){
		if ($j("#edit-address-panel").is(":hidden")) {
			$j("#edit-address-panel").slideDown("fast", function(){
				$j("#add-address-button").toggle();
				$j("#save-address-button").toggle();
				$j("#cancel-address-button").toggle();
			});
		}else{
			$j("#edit-address-panel").slideUp("fast", function(){
				$j("#add-address-button").toggle();
				$j("#save-address-button").toggle();
				$j("#cancel-address-button").toggle();
			});
		}
	}
	
	function toggleAlerts(event){
		if($j("#enable-alerts-checkbox").is(":checked")){
			$j("#alert-address-select").removeAttr("disabled");
		}else{
			$j("#alert-address-select").attr("disabled","disabled");
		}
		alertsChanged();
	}

	function alertsChanged(){
		DWRMessagingSettingsService.setAlertSettings($j("#enable-alerts-checkbox").is(":checked"), $j("#alert-address-select").val());
	}
	
	function cloneAddressRow(adrId){
		adrString = "<tr class=\"address-row\" id=\"addressRow#\"><td id=\"address-row-address#\"></td><td id=\"address-row-type#\"></td><td id=\"address-row-preferred#\"></td><td class=\"address-row-actions\"><div class=\"address-action-buttons\"><a href=\"\" onclick=\"editClicked(this.id)\" id=\"edit-link#\"><img src=\"<openmrs:contextPath/>/moduleResources/messaging/images/pencil.png\" alt=\"Edit\"/></a><a href=\"\" onclick=\"deleteClicked(this.id)\" id=\"delete-link#\"><img src=\"<openmrs:contextPath/>/moduleResources/messaging/images/delete.png\" alt=\"Delete\"/></a></div></td></tr>"
		return adrString.replace(new RegExp("#",'g'),adrId);
	}
</script>