<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<div id="patientMessagesBoxHeader" class="boxHeader"><spring:message code="messaging.messages.box.title" /></div>
<div id="patientMessagesBox" class="box">

<table id="messageTable">
	<thead>
		<tr>
			<th>To</th>
			<th>From</th>
			<th>Content</th>
			<th>Date Sent</th>
			<th>Service</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="message" items="${ model.messages}">
			<tr>
				<td><msg:formatAddress message="${message}" toOrFrom="to"/></td>
				<td><msg:formatAddress message="${message}" toOrFrom="from"/></td>
				<td>${message.content}</td>
				<td><openmrs:formatDate date="${message.dateSent}" /></td>
				<td><msg:serviceName message="${message}"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

</div>
<br />
<div id="patientAddressesBoxHeader" class="boxHeader"><spring:message code="messaging.addresses.box.title" /></div>
<div id="patientAddressesBox" class="box">

<h3>Manage Your Addresses</h3>
<table border="1" class="rowed grey">
	<thead>
		<tr>
			<th>Type</th>
			<th>Address</th>
			<th>Date Created</th>
			<th>Preferred</th>
		</tr>
	</thead>
	<tbody id="addressbody">
		<tr id="pattern" style="display: none;">
			<td><span id="tableType">Type</span><br />
			</td>
			<td><span id="tableAddress">Address</span></td>
			<td><span id="tableDateCreated">Date Created</span></td>
			<td><span id="tablePreferred">Preferred</span></td>
			<td>
				<input id="edit" type="button" value="Edit" onclick="editClicked(this.id)" />
				<input id="delete" type="button" value="Delete" onclick="deleteClicked(this.id)" />
			</td>
		</tr>
	</tbody>
</table>

<span id="editSpan">
<h3>Add an Address</h3>
</span>
<table class="plain">
	<tr>
		<td>Type</td>
		<td><select id="typeName" onchange="selectChanged()">
			<c:forEach var="type" items="${addressTypes}">
				<option value="${type}">${type}</option>
			</c:forEach>
		</select></td>
		<td>Address</td>
		<td>
			<input id="address" type="text" size="20" /> 
			<span id="id" style="display: none">-1</span>
		</td>
		<td id="passwordLabel" style="display: none">Password</td>
		<td><input id="password" type="password" size="20" style="display: none" /></td>
		<td>Preferred</td>
		<td><input id="preferred" type="checkbox" /></td>
		<td colspan="2" align="right">
			<input type="button" value="Save" onclick="writeAddress()" /> 
			<input type="button" value="Clear" onclick="clearAddress()" />
		</td>
	</tr>
</table>
</div>

<br />

<div id="sendAMessageBoxHeader" class="boxHeader"><spring:message code="messaging.send.message.box.title" /></div>
<div id="sendAMessageBox" class="box">
	<form method="post" action="<openmrs:contextPath/>/module/messaging/admin/sendMessage.form">
		<input type="hidden" name="recipient" value="${patient.patientId}"/>
		<input type="hidden" name="returnUrl" value="/patientDashboard.form?patientId=${patient.patientId}"/>
		<label for="fromAddress">From</label>
		<select name="fromAddress" id="fromAddress">
			<c:forEach var="fromType" items="${model.fromAddresses}">
				<optgroup label="${fromType.typeName}">
					<c:forEach var="fromAddress" items="${fromType.addresses}">
						<option value="${fromAddress}">${fromAddress}</option>
					</c:forEach>
				</optgroup>
			</c:forEach>
		</select>
		<label for="toAddress">to</label>
		<select name="toAddress" id="toAddress">
			<c:forEach var="toType" items="${model.toAddresses}">
				<optgroup label="${toType.typeName}">
					<c:forEach var="toAddress" items="${toType.addresses}">
						<option value="${toAddress}">${toAddress}</option>
					</c:forEach>
				</optgroup>
			</c:forEach>
		</select>
		<br/>Message:<br/>
		<textarea rows="6" cols="40" name="content"></textarea><br/><br/>
		<input type="submit" value="Send" />
	</form>
</div>
  
<script type="text/javascript">
	window.onload = init;
	
	var addressCache = { };
	var viewed = -1;
	function init(){
		//dwr.util.useLoadingMessage();
		dwr.engine.beginBatch();
		fillTable();
		selectChanged();
		dwr.engine.endBatch();
	}
	
	function fillTable() {
		DWRMessagingService.getAddressesForCurrentUser(function(addresses) {
		// Delete all the rows except for the "pattern" row
		dwr.util.removeAllRows("addressbody", { filter:function(tr) {return (tr.id != "pattern");}});
		// Create a new set cloned from the pattern row
		var address, id;
		for (var i = 0; i < addresses.length; i++) {
			address = addresses[i];
		    id = address.id;
		    dwr.util.cloneNode("pattern", { idSuffix:id });
		    dwr.util.setValue("tableType" + id, address.typeName);
		    dwr.util.setValue("tableAddress" + id, address.address);
		    dwr.util.setValue("tableDateCreated" + id, "");
		    dwr.util.setValue("tablePreferred" + id, address.preferred?"*":"");
		    $("pattern" + id).style.display = "table-row";
		    addressCache[id] = address;
		}
		});
	}
	
	function editClicked(eleid) {
		// we were an id of the form "edit{id}", eg "edit42". We lookup the "42"
		var address = addressCache[eleid.substring(4)];
		dwr.util.setValues(address);
		passwordRequired(address.typeName);
		document.getElementById("editSpan").innerHTML = "<h3>Editing \"" + address.address + "\"</h3>";
	}
	
	function deleteClicked(eleid) {
		// we were an id of the form "delete{id}", eg "delete42". We lookup the "42"
		var address = addressCache[eleid.substring(6)];
		if (confirm("Are you sure you want to delete address " + address.address + "?")) {
	    	dwr.engine.beginBatch();
	    	DWRMessagingService.deleteAddress(address.id);
	    	fillTable();
	    	dwr.engine.endBatch();
	    	clearAddress();
	  	}
	}
	
	function clearAddress() {
	 	viewed = -1;
		dwr.util.setValues({ id:-1, typeName:null, address:null, password:null, preferred:null, password:null});
		document.getElementById("editSpan").innerHTML = "<h3>Add an Address</h3>";
	}

	function writeAddress() {
		  var address = { id:viewed, typeName:null, address:null, password:null, preferred:null, dateCreated:null };
		  dwr.util.getValues(address);
		  dwr.engine.beginBatch();
		  DWRMessagingService.saveOrUpdateAddressForCurrentUser(address);
		  fillTable();
		  dwr.engine.endBatch();
		  clearAddress();
	}

	function selectChanged(){
		var select = document.getElementById("typeName");
		passwordRequired(select.options[select.selectedIndex].value);
	}
	
	function passwordRequired(addressType){
		DWRMessagingService.requiresPassword(addressType,function(required) {
			if(required){
				document.getElementById("password").style.display = "inline";
				document.getElementById("passwordLabel").style.display = "inline";
			}else{
				document.getElementById("password").style.display = "none";
				document.getElementById("passwordLabel").style.display = "none";
			}
		});
	}