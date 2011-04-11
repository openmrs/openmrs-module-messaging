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
						<tbody id="address-table-body">
							<tr class="address-row" id="addressRow" style="display:none;">
								<td id="address-row-address"></td>
								<td id="address-row-type"></td>
								<td id="address-row-preferred"></td>
								<td class="address-row-actions">
									<div class="address-action-buttons">
										<a href="#" onclick="editClicked(this.id)" id="edit-link">
											<img src="<openmrs:contextPath/>/moduleResources/messaging/images/pencil.png" alt="Edit"/>
										</a>
										<a href="#" onclick="deleteClicked(this.id)" id="delete-link">
											<img src="<openmrs:contextPath/>/moduleResources/messaging/images/delete.png" alt="Delete"/>
										</a>
									</div>
								</td>
							</tr>
						</tbody>
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
				<input type="checkbox" id="enable-alerts-checkbox">Alert me when I have new Omail at</input>
				<select id="alert-address-select" disabled="true">
					<option >+18069876543</option>
					<option>dieterich.lawson@gmail.com</option>
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

<script type="text/javascript">
	window.onload = init;
	var viewed = -1;
	var addressCache = { };
	var protocolNames= {"org.openmrs.module.messaging.sms.SmsProtocol":"SMS","org.openmrs.module.messaging.omail.OMailProtocol":"OMail"};
	
	function init(){
		//add the "Add Address" event listener
		document.getElementById('add-address-button').addEventListener('click',addAddressClick,false);
		document.getElementById('save-address-button').addEventListener('click',saveAddressClick,false);
		document.getElementById('cancel-address-button').addEventListener('click',cancelAddressClick,false);
		document.getElementById('enable-alerts-checkbox').addEventListener('click',toggleAlerts,false);
		//watermark the address button
		$('#address-textbox').watermark('Address');
		fillAddressTable();
		fillAlertAddressSelect();
	}

	function fillAddressTable(){
		DWRMessagingAddressService.getAllAddressesForCurrentUser(function(addresses){
			dwr.util.removeAllRows("address-table-body", { filter:function(tr) {return (tr.id != "addressRow");}});
			var address, id;
			// iterate through the messages, cloning the pattern row
			// and placing each message values into that row
			for (var i = 0; i < addresses.length; i++) {
				address = addresses[i];
			    id = address.id;
			    dwr.util.cloneNode("addressRow", { idSuffix:id });
			    dwr.util.setValue("address-row-address" + id, address.address);
			    dwr.util.setValue("address-row-type" + id, protocolNames[address.protocolClass]);
			    dwr.util.setValue("address-row-preferred" + id, address.preferred?"*":"");
				document.getElementById("addressRow" + id).style.display = "table-row";
				addressCache[id] = address;
			}
		});
	}

	function fillAlertAddressSelect(){
		DWRMessagingAddressService.getAllAddressesForCurrentUser(function(addresses) {
			dwr.util.removeAllOptions("alert-address-select");
			dwr.util.addOptions("alert-address-select",addresses,"address");
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
		if ($("#edit-address-panel").is(":hidden")){
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
	    	clearAddress();
			if ($("#edit-address-panel").is(":hidden")==false){
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
		if ($("#edit-address-panel").is(":hidden")) {
			$("#edit-address-panel").slideDown("fast", function(){
				$("#add-address-button").toggle();
				$("#save-address-button").toggle();
				$("#cancel-address-button").toggle();
			});
		}else{
			$("#edit-address-panel").slideUp("fast", function(){
				$("#add-address-button").toggle();
				$("#save-address-button").toggle();
				$("#cancel-address-button").toggle();
			});
		}
	}
	
	function toggleAlerts(event){
		if(document.getElementById("enable-alerts-checkbox").checked){
			document.getElementById("alert-address-select").disabled=false;
		}else{
			document.getElementById("alert-address-select").disabled=true;
		}
		
	}
</script>