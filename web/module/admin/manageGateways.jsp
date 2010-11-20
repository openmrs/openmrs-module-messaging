<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Manage Messaging Gateways</h2><br/>

<div id="modemsBoxHeader" class="boxHeader">Phones and Modems <img id="modemsStatusImg" src="/openmrs/moduleResources/messaging/images/bullet_red.png"/></div>
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

<div id="googleVoiceBoxHeader" class="boxHeader">Google Voice <img id="googleVoiceStatusImg" src="/openmrs/moduleResources/messaging/images/bullet_red.png" style=""/></div>
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
				$(prefix+"StatusImg").src="/openmrs/moduleResources/messaging/images/bullet_red.png";
				callback(false);
			}else{
				$(prefix+"StartStopButton").innerHTML="Stop Gateway";
				$(prefix+"StatusImg").src="/openmrs/moduleResources/messaging/images/bullet_green.png";
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
		$(prefix +"StatusImg").src="/openmrs/moduleResources/messaging/images/ajax-loader.gif";
		$(prefix +"StartStopButton").innerHTML="Stopping Gateway";
		$(prefix +"StartStopButton").disabled=true;
		DWRGatewayStatusService.stopGateway(classPrefix + gatewayId, function(){ updateStatus(gatewayId,prefix,callback);});
	}

	function startGateway(gatewayId, prefix, callback){
		if(!callback){
			callback = function(param){};
		}
		$(prefix +"StatusImg").src="/openmrs/moduleResources/messaging/images/ajax-loader.gif";
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
		
</script>