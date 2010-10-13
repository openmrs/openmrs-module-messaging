<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingAddressService.js"></script>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h3>Manage Your Addresses</h3>
<table border="1" class="rowed grey">
  <thead>
    <tr>
		<th>Type</th>
		<th>Address</th>
		<th>Date Created</th>
		<th>Preferred</th>
		<th>Public</th>
	</tr>
  </thead>
  <tbody id="addressbody">
    <tr id="pattern" style="display:none;">
      <td><span id="tableType">Type</span><br/></td>
      <td><span id="tableAddress">Address</span></td>
      <td><span id="tableDateCreated">Date Created</span></td>
      <td><span id="tablePreferred">Preferred</span></td>
      <td><span id="tableFindable">Public</span></td>
      <td>
        <input id="edit" type="button" value="Edit" onclick="editClicked(this.id)"/>
        <input id="delete" type="button" value="Delete" onclick="deleteClicked(this.id)"/>
      </td>
    </tr>
  </tbody>
</table>

<span id="editSpan"><h3>Add an Address</h3></span>
<table class="plain">
  <tr>
  	<td>Type</td>
  	<td>
  		<select id="protocolId">
			<c:forEach var="protocolName" items="${protocols}">
				<option value="${protocolName}">${protocolName}</option>
			</c:forEach>
		</select>
	</td>
    <td>Address</td>
    <td>
    	<input id="address" type="text" size="20"/>
		<span id="messagingAddressId" style="display:none">-1</span>
    </td>
    <td>Preferred</td>
    <td><input id="preferred" type="checkbox"/></td>
    <td>Public</td>
    <td><input id="findable" type="checkbox"/></td>
    <td colspan="2" align="right">
      <input type="button" value="Save" onclick="writeAddress()"/>
      <input type="button" value="Clear" onclick="clearAddress()"/>
   </td>
  </tr>
  </table>
  
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
		DWRMessagingAddressService.getAllAddressesForCurrentUser(function(addresses) {
		// Delete all the rows except for the "pattern" row
		dwr.util.removeAllRows("addressbody", { filter:function(tr) {return (tr.id != "pattern");}});
		// Create a new set cloned from the pattern row
		var address, id;
		for (var i = 0; i < addresses.length; i++) {
			address = addresses[i];
		    id = address.messagingAddressId;
		    dwr.util.cloneNode("pattern", { idSuffix:id });
		    dwr.util.setValue("tableType" + id, address.protocolId);
		    dwr.util.setValue("tableAddress" + id, address.address);
		    dwr.util.setValue("tableDateCreated" + id, "");
		    dwr.util.setValue("tablePreferred" + id, address.preferred?"*":"");
		    dwr.util.setValue("tableFindable" + id, address.findable?"*":"");
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
	    	DWRMessagingAddressService.deleteAddress(address.messagingAddressId);
	    	fillTable();
	    	dwr.engine.endBatch();
	    	clearAddress();
	  	}
	}
	
	function clearAddress() {
	 	viewed = -1;
		dwr.util.setValues({ messagingAddressId:-1, protocolId:null, address:null, preferred:null, findable:null, dateCreated:null });
		document.getElementById("editSpan").innerHTML = "<h3>Add an Address</h3>";
	}

	function writeAddress() {
		  var address = { messagingAddressId:viewed, protocolId:null, address:null, preferred:null, findable:null, dateCreated:null };
		  dwr.util.getValues(address);
		  dwr.engine.beginBatch();
		  DWRMessagingAddressService.saveOrUpdateAddressForCurrentUser(address);
		  fillTable();
		  dwr.engine.endBatch();
		  clearAddress();
	}
</script>