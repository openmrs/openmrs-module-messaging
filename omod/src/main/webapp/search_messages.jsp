<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<link rel="stylesheet" href="<openmrs:contextPath/>/moduleResources/messaging/css/search_messages.css" type="text/css"/>
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
	<td id="inbox">
		<div id="search-bar-container">
			<h3 id="search-label">Search</h3>
						<input id="search-textbox" type="text" value="${searchString}"></input>
			<c:if test="${searchingInbox == 'true'}">
				<input type="radio" name="inbox-group" id="search-inbox-check" value="inbox" checked/>Inbox</input>
			</c:if>
			<c:if test="${searchingInbox != 'true'}">
				<input type="radio" name="inbox-group" id="search-inbox-check" value="inbox"/>Inbox</input>
			</c:if>
			<c:if test="${searchingSent == 'true'}">
				<input type="radio" name="inbox-group" id="search-sent-check" value="sent" checked/>Sent Messages</input>
			</c:if>
			<c:if test="${searchingSent != 'true'}">
				<input type="radio" name="inbox-group" id="search-sent-check" value="sent"/>Sent Messages</input>
			</c:if>
			<button onclick="search(true)">Search</button>
		</div>
		<div id="message-table-container">
			<div id="loading-container">
				<div id="inner-loading-container">
					<span id="loading-text">Loading...</span>
					<img src="<openmrs:contextPath/>/moduleResources/messaging/images/ajax-loading-bar.gif"/>
				</div>
			</div>
			<table id="messages-table" class="message-table">
				<thead>
					<tr>
						<th>From</th>
						<th>Message</th>
						<th>Date</th>
					</tr>
				</thead>
				<tbody id="messages-table-body">		
				</tbody>
			</table>
			<div id="paging-controls-container">
				<span id="paging-controls">
					<span id="paging-info">
						<span id="paging-start">1</span> to 
						<span id ="paging-end">15</span> of 
						<span id="paging-total">234</span>
					</span>
					<a href="#" id="previous-page" onclick="pageToPreviousPage()">&lt;</a>
					<span id="current-page">1</span>
					<a href="#" id="next-page" onclick="pageToNextPage()">&gt;</a>
				</span>
			</div>
		</div>
		<div id="message-panel">
		<div id="message-info-panel" class="boxHeader">
			<table id="message-header-table">
				<tr><td class="header-label">From: </td><td class="header-info" id="header-from"></td></tr>
				<tr><td class="header-label">Subject: </td><td class="header-info" id="header-subject"></td></tr>
				<tr><td class="header-label">Date: </td><td class="header-info" id="header-date"></td></tr>
				<tr><td class="header-label">To: </td><td class="header-info" id="header-to"></td></tr>
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
<openmrs:htmlInclude file="/dwr/engine.js"/>
<openmrs:htmlInclude file="/dwr/util.js"/>
<script src="<openmrs:contextPath/>/dwr/interface/DWRModuleMessageService.js"></script>

<script type="text/javascript">	
	window.onload = init;
	
	var messageCache = { };
	var messageTableVisible= true;
	var pageNum=0;
	var pageSize=10;
	
	function init() {
		$j("#messages-table-body tr").live("click",rowClicked);
		if($j("#search-textbox").val() != ""){
			search();
		}
	}
	
	function rowClicked(e){
		var who = e.target || e.srcElement;
		var id = who.id.substring(12);
		var message = messageCache[id];
		$j("#header-from").html(message.sender);
		$j("#header-subject").html(message.subject);
		$j("#header-date").html(message.date);
		$j("#header-to").html(message.recipients);
		$j("#message-text-panel").html(message.content);
		$j("#messages-table-body").children().removeClass("highlight-row");
		$j("#pattern"+id).addClass("highlight-row");	
		$j("#reply-buttons").css("visibility","visible");
		$j(".header-label").css("visibility","visible");
	}
	
	function search(resetPaging){
		if(resetPaging){
			pageNum = 0;
		}
		var searchString = $j("#search-textbox").val();
		var searchInbox = $j("#search-inbox-check").is(":checked");
		var searchOutbox = $j("#search-sent-check").is(":checked");
		DWRModuleMessageService.searchMessagesForAuthenticatedUser(pageNum,pageSize,searchString,searchInbox,searchOutbox,function(messageSet){
			dwr.util.removeAllRows("messages-table-body", { filter:function(tr) {return (tr.id != "pattern");}});
			var message;
			var messages = messageSet.messages;
			// iterate through the messages, cloning the pattern row
			// and placing each message values into that row
			for (var i = 0; i < messages.length; i++) {
				message = messages[i];
			    $j(createMessageRow(message.id)).appendTo("#messages-table-body");
			    $j("#message-from" + message.id).html(message.sender);
			    $j("#message-subj" + message.id).html(message.subject);
			    $j("#message-date" + message.id).html(message.time+ " " + message.date);
			    messageCache[message.id] = message;
			}
			pageNum = messageSet.pageNumber;
			pageSize = messageSet.pageSize;
			setPagingControls(messageSet);
		});
	}
	
	function messageClicked(event){}
	
	function replyClicked(event){}
	
	function replyAllClicked(event){}

	function setPagingControls(messageSet){
		$j("#paging-start").html((messageSet.pageNumber * messageSet.pageSize) + 1);
		$j("#paging-end").html((messageSet.pageNumber * messageSet.pageSize)  + messageSet.messages.length);
		$j("#paging-total").html(messageSet.total);
		$j("#current-page").html(messageSet.pageNumber+1);
		//enable or disable the paging controls properly
		if(messageSet.pageNumber === 0){
			$j('#previous-page').attr('class','disabled');
		}else{
			$j('#previous-page').attr('class','');
		}
		
		if(messageSet.pageSize > messageSet.messages.length || ((messageSet.pageNumber * messageSet.pageSize)  + messageSet.messages.length) === messageSet.total){
			$j('#next-page').attr('class','disabled');
		}else{
			$j('#next-page').attr('class','');
		}
	}

	function pageToPreviousPage(){
		if($j('#previous-page').hasClass('disabled')){
			return false;
		}else{
			pageNum--;
			search(false);
		}
	}

	function pageToNextPage(){
		if($j('#next-page').hasClass('disabled')){
			return false;
		}else{
			pageNum++;
			search(false);
		}
	}

	function createMessageRow(mesgId){
		msgString = "<tr class=\"message-row\" id=\"pattern#\"><td class=\"message-row-from\" id=\"message-from#\"></td><td class=\"message-row-subject\" id=\"message-subj#\"></td><td class=\"message-row-date\" id=\"message-date#\"></td></tr>";
		return msgString.replace(new RegExp("#",'g'),mesgId);
	}
</script>