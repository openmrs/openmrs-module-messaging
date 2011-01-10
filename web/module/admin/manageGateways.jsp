<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<openmrs:require privilege="Manage Messaging Gateways" otherwise="/login.htm" redirect="/module/messaging/manageGateways.form"/>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Manage Messaging Gateways</h2><br/>

<div id="modemsBoxHeader" class="boxHeader"> 
	<img id="modemsStatusImg" src="<openmrs:contextPath/>/moduleResources/messaging/images/bullet_red.png" style="vertical-align:bottom;"/>
	Phones and Modems
</div>
<div id="modemsBox" class="box">
	Phones
	<table id="modemsTable" cellpadding="2" border="1">
		<thead>
			<tr>
				<td>Port</td>
				<td>Number</td>
				<td>Serial</td>
				<td>Model</td>
				<td>Status</td>
			</tr>
		</thead>
		<tbody id="modemsTableBody"></tbody>
	</table><br/><br/>
	Bulk SMS Accounts
	<table id="bulkSmsTable" cellpadding="2" border="1">
		<thead>
			<tr>
				<td>Service</td>
				<td>Username</td>
				<td>Status</td>
				<td>Actions</td>
			</tr>
		</thead>
		<tbody id="modemsTableBody"></tbody>
	</table><br/><br/>
	<button id="modemsStartStopButton" onclick="toggleSmsLibGateway()"></button>
</div>

<br/>

<div id="googleVoiceBoxHeader" class="boxHeader"> 
	<img id="googleVoiceStatusImg" src="<openmrs:contextPath/>/moduleResources/messaging/images/bullet_red.png" style="vertical-align:bottom;"/>
	Google Voice
</div>
<div id="googleVoiceBox" class="box">
	Google Voice username: ${googleVoiceUsername}<br/><br/>
	Change the Google Voice login:
	<form method="post" action="<openmrs:contextPath/>/module/messaging/changeGoogleVoiceCreds.form">
		Username:  <input type="text" name="username"/><br/>
		Password:  <input type="password" name="password1"/><br/>
		Confirm Password:  <input type="password" name="password2"/><br/>
		<input type="submit" value="Save Changes" />
	</form>
	<br/>
	<button id="googleVoiceStartStopButton" onclick="toggleGatewayStatus('googlevoice.GoogleVoiceGateway','googleVoice')"></button>
</div>

<br />

<b id="emailBoxHeader" class="boxHeader">
	<img id="emailStatusImg" src="<openmrs:contextPath/>/moduleResources/messaging/images/bullet_red.png" style="vertical-align:bottom;"/>
	Email
</b>
<div id="emailBox" class="box">
	Configure Email accounts:
	<form method="post" action="<openmrs:contextPath/>/module/messaging/changeEmailCreds.form">
		<fieldset style="padding:1em;margin:1em;">
			<legend>Outgoing Message Format</legend>
			<table>
				<tr>
					<td><label for="messageSubject">Subject Line:</label></td>
					<td><input type="text" name="messageSubject" value="${emailMessageSubject}"/></td>
				</tr>
				<tr>
					<td style="vertical-align:top;"><label for="messageSignature">Message Signature:</label></td>
					<td><textarea name="messageSignature" rows="6" cols="60">${emailMessageSignature}</textarea></td>
				</tr>
			</table>
		</fieldset>
		<fieldset style="padding:1em;margin:1em;">
			<legend>Incoming Email Server</legend>
			<table>
				<tr>
					<td><label for="inprotocol">Protocol:</label></td>
					<td>
						<select name="inprotocol">
							<option value="pop3">POP3</option>
							<option value="pop3s">POP3S</option>
							<option value="imap">IMAP</option>
							<option value="imaps">IMAPS</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><label for="inhost">Host:</label></td>
					<td><input type="text" name="inhost" value="${emailInHost}"/></td>
				</tr>
				<tr>
					<td><label for="inport">Port:</label></td>
					<td><input type="text" name="inport" size="3" value="${emailInPort}"/></td>
				</tr>
				<tr>
					<td><label for="inauth">Authentication Required:</label></td>
					<td>
						<c:if test="${emailInAuth == 'true'}">
							<input type="checkbox" name="inauth" checked/>
						</c:if>
						<c:if test="${emailInAuth != 'true'}">
							<input type="checkbox" name="inauth"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td><label for="intls">Use TLS:</label></td>
					<td>
						<c:if test="${emailInTLS == 'true'}">
							<input type="checkbox" name="intls" checked/>
						</c:if>
						<c:if test="${emailInTLS != 'true'}">
							<input type="checkbox" name="intls"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td><label for="inusername">Username:</label></td>
					<td><input type="text" name="inusername" value="${emailInUsername}"/></td>
				</tr>
				<tr>
					<td><label for="inpwd1">Password:</label></td>
					<td><input class="emailpassword" type="password" name="inpwd1" value="########"/></td>
				</tr>
				<tr>
					<td><label for="inpwd2">Confirm Password:</label></td>
					<td><input class="emailpassword" type="password" name="inpwd2" value="########"/></td>
				</tr>
			</table>
			<input type="hidden" name="inpwdchanged" value="false"/>
		</fieldset>
		<fieldset style="padding:1em;margin:1em;">
			<legend>Outgoing Email Server</legend>
			<c:if test="${emailOutUseDefault == 'true'}">
				<input type="checkbox" name="usedefaultout" checked/>
			</c:if> 
			<c:if test="${emailOutUseDefault != 'true'}">
				<input type="checkbox" name="usedefaultout"/>
			</c:if>
			<label for="usedefaultout">Use OpenMRS Default Server</label>
			<br /><br />
			<table id="emailOut">
				<tr>
					<td><label for="outprotocol">Protocol:</label></td>
					<td>
						<select name="outprotocol">
							<option value="smtp">SMTP</option>
							<option value="smtps">SMTPS</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><label for="outhost">Host:</label></td>
					<td><input type="text" name="outhost" value="${emailOutHost}"/></td>
				</tr>
				<tr>
					<td><label for="outport">Port:</label></td>
					<td><input type="text" name="outport" size="3" value="${emailOutPort}"/></td>
				</tr>
				<tr>
					<td><label for="outauth">Authentication Required:</label></td>
					<td>
						<c:if test="${emailOutAuth == 'true'}">
							<input type="checkbox" name="outauth" checked/>
						</c:if>
						<c:if test="${emailOutAuth != 'true'}">
							<input type="checkbox" name="outauth"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td><label for="outtls">Use TLS:</label></td>
					<td>
						<c:if test="${emailOutTLS == 'true'}">
							<input type="checkbox" name="outtls" checked/>
						</c:if>
						<c:if test="${emailOutTLS != 'true'}">
							<input type="checkbox" name="outtls"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td><label for="outfrom">Sending Address:</label></td>
					<td><input type="text" name="outfrom" value="${emailOutFrom}"/></td>
				</tr>
				<tr>
					<td><label for="outusername">Username:</label></td>
					<td><input type="text" name="outusername" value="${emailOutUsername}"/></td>
				</tr>
				<tr>
					<td><label for="outpwd1">Password:</label></td>
					<td><input class="emailpassword" type="password" name="outpwd1" value="########"/></td>
				</tr>
				<tr>
					<td><label for="outpwd2">Confirm Password:</label></td>
					<td><input class="emailpassword" type="password" name="outpwd2" value="########"/></td>
				</tr>
			</table>
			<input type="hidden" name="outpwdchanged" value="false"/>
		</fieldset>
		<input type="submit" value="Save Changes" />
	</form>
	<br/>
	<button id="emailStartStopButton" onclick="toggleGatewayStatus('email.EmailGateway','email')"></button>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<script src="<openmrs:contextPath/>/dwr/interface/DWRGatewayStatusService.js"></script>
<script src="<openmrs:contextPath/>/dwr/interface/DWRSmsLibGatewayService.js"></script>

<script type="text/javascript">
	window.onload = init;

	var classPrefix = "org.openmrs.module.messaging.";

	function init(){
		updateStatus("sms.SmsLibGateway","modems");
		updateStatus("googlevoice.GoogleVoiceGateway","googleVoice");
		updateStatus("email.EmailGateway","email");
		updateModemList();
	}

	function updateStatus(gatewayId,prefix,callback){
		if(!callback){
			callback = function(param){};
		}
		DWRGatewayStatusService.isGatewayActive(classPrefix + gatewayId, function(isActive){
			$(prefix+"StartStopButton").disabled=false;
			if(!isActive){
				$(prefix+"StartStopButton").innerHTML="Start Gateway";
				$(prefix+"StatusImg").src="<openmrs:contextPath/>/moduleResources/messaging/images/bullet_red.png";
				callback(false);
			}else{
				$(prefix+"StartStopButton").innerHTML="Stop Gateway";
				$(prefix+"StatusImg").src="<openmrs:contextPath/>/moduleResources/messaging/images/bullet_green.png";
				callback(true);
			}
		});
	}

	function toggleGatewayStatus(gatewayId, prefix, callback){
		if(!callback){
			callback = function(param){};
		}
		DWRGatewayStatusService.isGatewayActive(classPrefix + gatewayId, function(isActive){
			if(isActive){
				stopGateway(gatewayId, prefix,callback);
			}else{
				startGateway(gatewayId, prefix,callback);
			}
		});
	}
	
	function stopGateway(gatewayId, prefix, callback){
		if(!callback){
			callback = function(param){};
		}
		$(prefix +"StatusImg").src="<openmrs:contextPath/>/moduleResources/messaging/images/ajax-loader.gif";
		$(prefix +"StartStopButton").innerHTML="Stopping Gateway";
		$(prefix +"StartStopButton").disabled=true;
		DWRGatewayStatusService.stopGateway(classPrefix + gatewayId, function(){ updateStatus(gatewayId,prefix,callback);});
	}

	function startGateway(gatewayId, prefix, callback){
		if(!callback){
			callback = function(param){};
		}
		$(prefix +"StatusImg").src="<openmrs:contextPath/>/moduleResources/messaging/images/ajax-loader.gif";
		$(prefix +"StartStopButton").innerHTML="Starting Gateway";
		$(prefix +"StartStopButton").disabled=true;
		DWRGatewayStatusService.startGateway(classPrefix + gatewayId, function(){ updateStatus(gatewayId,prefix,callback);});
	}

	function updateModemList(){
		DWRSmsLibGatewayService.getConnectedModems(function(modems){
			dwr.util.removeAllRows("modemsTableBody");
			for(var i = 0; i < modems.length; i++){
				addModemRow(modems[i]);		
			}
		});
	}

	function addModemRow(modemBean){
		var newRow = document.getElementById("modemsTableBody").insertRow(-1);
		var portCell = newRow.insertCell(-1);
		portCell.innerHTML= modemBean.port;
		var numberCell = newRow.insertCell(-1);
		numberCell.innerHTML= modemBean.number;
		var serialCell = newRow.insertCell(-1);
		serialCell.innerHTML= modemBean.serial;
		var modelCell = newRow.insertCell(-1);
		modelCell.innerHTML= modemBean.model;
		var statusCell = newRow.insertCell(-1);
		statusCell.innerHTML= modemBean.status;
	}

	function toggleSmsLibGateway(){
		toggleGatewayStatus("sms.SmsLibGateway","modems", function(isStarted){
			updateModemList();
		});
	}
	
	// disable or enable the outgoing email parameters when usedefaultout is clicked
	function toggleOutgoingEmailInfo() {
		if ($j("#emailBox input[name=usedefaultout]").attr("checked")) {
			$j("#emailBox [name^='out']").attr("disabled", true);
			$j("#emailOut").hide("blind");
		} else {
			$j("#emailBox [name^='out']").removeAttr("disabled");
			$j("#emailOut").fadeIn();
		}
	}
	
	$j(document).ready(function(){
		// select incoming and outgoing server protocols
		$j("#emailBox select[name=inprotocol]").children().each(function(){ 
			if ($j(this).val() == "${emailInProtocol}")
				$j(this).attr("selected", true);
			else
				$j(this).removeAttr("selected");
		});
		$j("#emailBox select[name=outprotocol]").children().each(function(){ 
			if ($j(this).val() == "${emailOutProtocol}")
				$j(this).attr("selected", true);
			else
				$j(this).removeAttr("selected");
		});

		// set click event for usedefaultout checkbox
		$j("#emailBox input[name=usedefaultout]").click(toggleOutgoingEmailInfo);
		
		// set "password changed" hidden fields if one is changed
		$j("#emailBox input[name^=inpwd]").keyup(function(){ 
			$j("#emailBox input[name=inpwdchanged]").val("true"); 
		});
		$j("#emailBox input[name^=outpwd]").keyup(function(){ 
			$j("#emailBox input[name=outpwdchanged]").val("true"); 
		});
	});
		
</script>