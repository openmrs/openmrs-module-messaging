<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<link rel="stylesheet" href="<openmrs:contextPath/>/moduleResources/messaging/css/inbox.css" type="text/css"/>
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
		<c:if test="${messageNumber == 0 }">
			<div id="no-messages">
					<span id="no-messages-text">You don't have any messages yet.<br><br> Why don't you <a href="<openmrs:contextPath/>/module/messaging/compose_message.form">write</a> one?</span>
			</div>
		</c:if>
		<c:if test="${messageNumber >0 }">
		<div id="search-bar-container">
			<form method="post" action="<openmrs:contextPath/>/module/messaging/search.form">
				<input id="inbox-search" name="searchString" type="text" style="display:inline;"/>
				<input id="search-button" type="submit" value="Search"/>
				<input type="hidden" name="searchingInbox" value="true"/>
				<input type="hidden" name="searchingSent" value="false"/>
			</form>
		</div><br/>
		<div id="message-table-container">
			<table id="messages-table" class="message-table">
				<thead>
					<tr class="message-row">
						<th class="message-row-from">From</th>
						<th class="message-row-subject">Message</th>
						<th class="message-row-date">Date</th>
					</tr>
				</thead>
				<tbody id="messages-table-body">
					<tr class="message-row" id="pattern" style="display:none;">
						<td class="message-row-from" id="message-from"></td>
						<td class="message-row-subject" id="msg-subj-con"><span class="msg-subj" id="message-subj"></span><span class="msg-msg" id="message-mesg"></span></td>
						<td class="message-row-date" id="message-date"></td>
					</tr>			
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
		</c:if>
		<div id="message-panel">
		<div id="message-info-panel" class="boxHeader">
			<table id="message-header-table">
				<tr><td class="header-label">From: </td><td class="header-info" id="header-from"></td></tr>
				<tr><td class="header-label">Subject: </td><td class="header-info" id="header-subject"></td></tr>
				<tr><td class="header-label">Date: </td><td class="header-info" id="header-date"></td></tr>
				<tr><td class="header-label">To: </td><td class="header-info" id="header-to"></td></tr>
			</table>
			<div id="reply-buttons">
				<form method="post" action="<openmrs:contextPath/>/module/messaging/reply_to_message.form" style="display:inline;">
					<input type="hidden" name="replyToMessageId" value="" id="replyMessageId" style="display:inline;"/>
					<input type="hidden" name="replyAll" value="false" style="display:inline;"/>
					<input id="reply-button" type="submit" value="Reply" style="display:inline;"></input>
				</form>
				<form method="post" action="<openmrs:contextPath/>/module/messaging/reply_to_message.form" style="display:inline;">
					<input type="hidden" name="replyToMessageId" value="" id="replyAllMessageId" style="display:inline;"/>
					<input type="hidden" name="replyAll" value="true" style="display:inline;"/>
					<input id="reply-all-button" type="submit" value="Reply All" style="display:inline;"></input>
				</form>
			</div>
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
//	var $j = jQuery.noConflict();
	var messageCache = { };
	var messageTableVisible= true;
	var pageNum=0;
	var pageSize=10;
	
	function init() {
		//$j("#inbox-search").watermark("search inbox");
		$j("#messages-table-body *").live("click",rowClicked);
		fillMessageTable();
	}
	
	function rowClicked(e){
		var who = e.target || e.srcElement;
		var messageId = who.id.substring(12);
		var message = messageCache[messageId];
		if(message.read == false){
			DWRModuleMessageService.markMessageAsReadForAuthenticatedUser(message.id);
			message.read=true;
			$j("#pattern"+message.id).removeClass("unread-row");	
			$j("#pattern"+message.id).addClass("read-row");	
		}
		$j("#header-from").html(message.sender);
		$j("#header-subject").html(message.subject);
		$j("#header-date").html(message.date);
		$j("#header-to").html(message.recipients);
		$j("#message-text-panel").html(message.content);
		$j("#messages-table-body").children().removeClass("highlight-row");
		$j("#pattern"+message.id).addClass("highlight-row");	
		$j("#reply-buttons").css("visibility","visible");
		$j(".header-label").css("visibility","visible");
		$j("#replyAllMessageId").val(message.id);
		$j("#replyMessageId").val(message.id);
	}
	
	function fillMessageTable(){
		DWRModuleMessageService.getMessagesForAuthenticatedUserWithPageSize(pageNum,pageSize,true,function(messageSet){
			dwr.util.removeAllRows("messages-table-body", { filter:function(tr) {return (tr.id != "pattern");}});
			var message, messageId;
			var messages = messageSet.messages;
			// iterate through the messages, cloning the pattern row
			// and placing each message values into that row
			for (var i = 0; i < messages.length; i++) {
				message = messages[i];
			    //messageId = message.id;
			    $j(createMessageRow(message.id)).appendTo("#messages-table-body");
			    $j("#message-from" + message.id).html(message.sender);
				$j("#pattern"+message.id+" .msg-subj").html(message.subject);
				$j("#pattern"+message.id+" .msg-msg").html(" - "+ message.messageSnippet);
			    $j("#message-date" + message.id).html(message.time + " " + message.date);
				if(message.read){
					$j("#pattern" + message.id).addClass("read-row");
				}else{
					$j("#pattern" + message.id).addClass("unread-row");
				}
			    messageCache[message.id] = message;
			}
			pageNum = messageSet.pageNumber;
			pageSize = messageSet.pageSize;
			setPagingControls(messageSet);
		});
	}
	
	function createMessageRow(mesgId){
		msgString = "<tr class=\"message-row\" id=\"pattern#\"><td class=\"message-row-from\" id=\"message-from#\"></td><td class=\"message-row-subject\" id=\"msg-subj-con#\"><div class=\"subj-con\" id=\"subject-cont#\"><span class=\"msg-subj\" id=\"message-subj#\"></span><span class=\"msg-msg\" id=\"message-mesg#\"></span></div></td><td class=\"message-row-date\" id=\"message-date#\"></td></tr>";
		return msgString.replace(new RegExp("#",'g'),mesgId);
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
			fillMessageTable();
		}
	}

	function pageToNextPage(){
		if($j('#next-page').hasClass('disabled')){
			return false;
		}else{
			pageNum++;
			fillMessageTable();
		}
	}

	function toggleMessageLoading(){
		if(messageTableVisible){
			$j("#messages-table").hide();
			$j("#paging-controls-container").hide();
			$j("#loading-container").show();
			messageTableVisible=false;
		}else{
			$j("#messages-table").show();
			$j("#paging-controls-container").show();
			$j("#loading-container").hide();
			messageTableVisible=true;
		}
	}
</script>