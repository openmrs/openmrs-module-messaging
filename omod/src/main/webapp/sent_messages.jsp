<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<link rel="stylesheet" href="<openmrs:contextPath/>/moduleResources/messaging/css/sent_messages.css" type="text/css"/>
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
	<td id="sent-messages">
		<div id="search-bar-container">
			<input id="sent-messages-search" type="text"/>
		</div><br/>
		<div id="message-table-container">
			<table id="messages-table" class="message-table">
				<thead>
					<tr>
						<th>To</th>
						<th>Message</th>
						<th>Date</th>
					</tr>
				</thead>
				<tbody id="messages-table-body">
					<tr class="message-row" id="pattern" style="display:none;">
						<td class="message-row-to" id="message-dest"></td>
						<td class="message-row-subject" id="message-subj"></td>
						<td class="message-row-date" id="message-date"></td>
					</tr>			
				</tbody>
			</table>
		</div>
		<div id="message-panel">
		<div id="message-info-panel" class="boxHeader">
			<table id="message-header-table">
				<tr><td class="header-label">To: </td><td class="header-info" id="header-to"></td></tr>
				<tr><td class="header-label">Subject: </td><td class="header-info" id="header-subject"></td></tr>
				<tr><td class="header-label">Date: </td><td class="header-info" id="header-date"></td></tr>
			</table>
			<div style="clear:both;"></div>
		</div>
		<div id="message-text-panel">
		</div>
		</div>
	</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>

<script src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery-1.4.4.min.js"></script>
<script src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery.watermark.min.js"></script>
<openmrs:htmlInclude file="/dwr/engine.js"/>
<openmrs:htmlInclude file="/dwr/util.js"/>
<script src="<openmrs:contextPath/>/dwr/interface/DWRModuleMessageService.js"></script>

<script type="text/javascript">
	window.onload = init;
	
	var messageCache = { };

	var pageNum=0;
	var pageSize=10;
	
	function init() {
		$("#sent-messages-search").watermark("search sent messages");
		$("#messages-table-body tr").live("click",rowClicked);
		fillMessageTable();
	}
	
	function rowClicked(event){
		var id = event.srcElement.id.substring(12);
		var message = messageCache[id];
		document.getElementById("message-panel").style.display="";
		document.getElementById("header-subject").innerHTML = message.subject;
		document.getElementById("header-date").innerHTML = message.date;
		document.getElementById("header-to").innerHTML = message.recipients;
		document.getElementById("message-text-panel").innerHTML = message.content;
		$("#messages-table-body").children().removeClass("highlight-row");
		$("#pattern"+id).addClass("highlight-row");	
		$(".header-label").css("visibility","visible");
	}
	
	function fillMessageTable(){
		DWRModuleMessageService.getMessagesForAuthenticatedUserWithPageSize(pageNum,pageSize,false,function(messages){
			dwr.util.removeAllRows("messages-table-body", { filter:function(tr) {return (tr.id != "pattern");}});
			var message, id;
			// iterate through the messages, cloning the pattern row
			// and placing each message values into that row
			for (var i = 0; i < messages.length; i++) {
				message = messages[i];
			    id = message.id;
			    dwr.util.cloneNode("pattern", { idSuffix:id });
				dwr.util.setValue("message-dest" + id, message.recipients);
			    dwr.util.setValue("message-subj" + id, message.subject);
			    dwr.util.setValue("message-date" + id, message.date);
			    document.getElementById("pattern" + id).style.display = "table-row";
			    messageCache[id] = message;
			}
		});
	}
</script>