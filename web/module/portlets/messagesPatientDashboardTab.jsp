<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<script src="<openmrs:contextPath/>/dwr/interface/DWRModuleMessageService.js"></script>
<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingAddressService.js"></script>

<style>
	.align-right{
		float:right
	}
	.conversationBox {
		width:70%;
	}
	.conversation-header {
		margin:7px;
	}
	.message-list {
		background:white;
		padding:7px;
	}
	.from-message-row {
		margin:2px;
	}
	.to-message-row {
		margin:2px;
		
	}
	.time-message-row{
		color:#b0b5b0;
		margin:5px;
	}
	.message-sender {
		font-weight:bold;
	}
	.message-time {
		color:#b0b5b0;
	}
	.send-controls{
		padding:7px;
	}
	.send-box-cell{
		width:85%;
		padding-right:10px;
	}
	.send-box{
		width:100%;
	}
	
</style>
		<div class="conversationBox">
			<div class="boxHeader">
				<span style="margin:3px">Messages</span>
				<img id="refreshButton" class="align-right" src="../openmrs/moduleResources/messaging/images/refresh.png" onclick="fillMessageList()"/>
			</div>
			<div class="box">
				<div class="message-list" id="messageList">
					<div class="to-message-row" id="messagePattern" style="display:none" onmouseover="handleMouseOver(this.id)" onmouseout="handleMouseOut(this.id)">
						<span class="message-sender" id="messageSender"></span>
						<span class="message-text" id="messageText"></span>
						<span class="message-time" id="messageTime" style="display:none"></span>
					</div>
				</div>
			</div>
				<div class="boxHeader">
					<table>
						<tr>
							<td class="send-box-cell"><textarea class="send-box" rows="2" id="messageContentBox"></textarea></td>
							<td>
								<select id="toAddressSelect"></select><br/>
								<input class="send-button" type="submit" value="Send" onclick="sendMessage()"></input>
							</td>
						</tr>
						<tr>
							<td id="sendMessageResults"></td>
						
						</tr>
					</table>
				</div>
			</div>
<br/><br/>
<div class="boxHeader">
Manage Addresses
</div>		
<div class="box">
<table border="1" class="rowed grey">
  <thead>
    <tr>
		<th>Type</th>
		<th>Address</th>
		<th>Date Created</th>
		<th>Preferred</th>
		<th>Actions</th>
	</tr>
  </thead>
  <tbody id="addressbody">
    <tr id="pattern" style="display:none;">
      <td><span id="tableType">Type</span><br/></td>
      <td><span id="tableAddress">Address</span></td>
      <td><span id="tableDateCreated">Date Created</span></td>
      <td><span id="tablePreferred">Preferred</span></td>
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
			<c:forEach var="protocol" items="${protocols}">
				<option value="${protocol.protocolId}">${protocol.protocolName}</option>
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
    <td colspan="2" align="right">
      <input type="button" value="Save" onclick="writeAddress()"/>
      <input type="button" value="Clear" onclick="clearAddress()"/>
   </td>
  </tr>
  </table>
 </div>
 </div>
 
<script type="text/javascript">
	window.onload = init;
	
	var addressCache = { };
	var viewed = -1;
	var colors = ["red","#32CD32","blue","#FFD700","orchid","aqua","purple"];
	
	function init(){
		dwr.engine.beginBatch();
		fillMessageList();
		fillAddressesTable();
		fillToAddressSelect();
		dwr.engine.endBatch();
	}
	
	function fillAddressesTable() {
		//call the DWR service to get all the addresses for the patient that is being viewed
		DWRMessagingAddressService.getAllAddressesForPersonId(${patient.patientId},function(addresses) {
			// Delete all the rows except for the "pattern" row
			dwr.util.removeAllRows("addressbody", { filter:function(tr) {return (tr.id != "pattern");}});
			var address, id;
			// iterate through the addresses, cloning the pattern row
			// and placing each addresses values into that row
			for (var i = 0; i < addresses.length; i++) {
				address = addresses[i];
			    id = address.messagingAddressId;
			    dwr.util.cloneNode("pattern", { idSuffix:id });
			    dwr.util.setValue("tableType" + id, address.protocolId.substring(address.protocolId.lastIndexOf(".")+1).replace("Protocol",""));
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
		// put the address's values into the editing area
		dwr.util.setValues(address);
		// change the title of the editing area
		$("editSpan").innerHTML = "<h3>Editing \"" + address.address + "\"</h3>";
	}
	
	function deleteClicked(eleid) {
		// we were an id of the form "delete{id}", eg "delete42". We lookup the "42"
		var address = addressCache[eleid.substring(6)];
		//confirm the delete
		if (confirm("Are you sure you want to delete address " + address.address + "?")) {
	    	dwr.engine.beginBatch();
	    	DWRMessagingAddressService.deleteAddress(address.messagingAddressId);
	    	fillAddressesTable();
	    	fillToAddressSelect();
	    	dwr.engine.endBatch();
	    	clearAddress();
	  	}
	}
	
	//resets the editing area
	function clearAddress() {
	 	viewed = -1;
		dwr.util.setValues({ messagingAddressId:-1, address:null, preferred:null, dateCreated:null });
		$("editSpan").innerHTML = "<h3>Add an Address</h3>";
		
	}

	function writeAddress() {
		  var address = { messagingAddressId:viewed, protocolId:null, address:null, preferred:null, dateCreated:null };
		  dwr.util.getValues(address);
		  dwr.engine.beginBatch();
		  DWRMessagingAddressService.saveOrUpdateAddress(address,${patient.patientId});
		  fillAddressesTable();
		  fillToAddressSelect();
		  dwr.engine.endBatch();
		  clearAddress();
		  
	}
	
	function fillMessageList() {
		$("refreshButton").src = "../openmrs/moduleResources/messaging/images/ajax-loader.gif";
		DWRModuleMessageService.getMessagesForPatient(${patient.patientId}, function(messages) {
		// Delete all the message rows except for the patterns
		removeChildrenFromNode($('messageList'));		
		// Create a new set cloned from the pattern row
		var message, id;
		for (var i = 0; i < messages.length; i++) {
			message = messages[i];
		    id = message.id;
		    //clone the pattern node
		    dwr.util.cloneNode("messagePattern", { idSuffix:id });
		    //if the id is less than 0, then the object is a date separator row
		    if(id < 0){
				$("messagePattern"+id).className="time-message-row";
		    	$("messageSender"+id).style.fontWeight="normal";
		    	dwr.util.setValue("messageSender"+id, "");
			    dwr.util.setValue("messageText" + id,  message.date+" "+message.time);
			    dwr.util.setValue("messageTime" + id, "");
		    }else{ //otherwise, it's a message row
		    	dwr.util.setValue("messageText" + id, message.text);
		    	dwr.util.setValue("messageTime" + id, message.time + " via " + message.protocolName);
		    	//if the message is from OpenMRS then we need to set the sender
		    	// and the proper color
				if(message.fromOpenMRS){
					$("messagePattern"+id).className="from-message-row";
			    	$("messageSender"+id).style.color=colors[message.colorNumber];
				}
				dwr.util.setValue("messageSender"+id,message.sender+": ");
		    }
		    //make it visible
		    $("messagePattern" + id).style.display = "block";
		}
		$("refreshButton").src = "../openmrs/moduleResources/messaging/images/refresh.png";
		});
	}

	function fillToAddressSelect(){
		DWRMessagingAddressService.getAllAddressesForPersonId(${patient.patientId},function(addresses) {
			dwr.util.removeAllOptions("toAddressSelect");
			dwr.util.addOptions("toAddressSelect",addresses,"address");
		});
	}

	function sendMessage(){
		DWRModuleMessageService.sendMessage($("messageContentBox").value, $("toAddressSelect").value,null, ${patient.patientId}, null, null, true, function(output){
			$("sendMessageResults").innerHTML = output;
			fillMessageList();
			dwr.util.setValue("messageContentBox","");
		});
	}
		

	function removeChildrenFromNode(node){
		while(node.firstChild.id != "messagePattern"){
			node.removeChild(node.firstChild);
		}
	}

	function handleMouseOver(elementId){
		$("messageTime"+elementId.substring(14)).style.display="inline";
	}

	function handleMouseOut(elementId){
		$("messageTime"+elementId.substring(14)).style.display="none";
	}
</script>
	