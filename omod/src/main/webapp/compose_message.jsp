<%@ include file="/WEB-INF/template/include.jsp"%>

<link rel="stylesheet" href="<openmrs:contextPath/>/moduleResources/messaging/css/compose_message.css" type="text/css"/>

<!-- YUI Text Editor includes -->
<link rel="stylesheet" type="text/css" href="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/skin.css">
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/yahoo-dom-event.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/element-min.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/container_core-min.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/menu-min.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/button-min.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/yui-text-editor/editor-min.js"></script>
<script type="text/javascript">	
	var myEditor = new YAHOO.widget.SimpleEditor('writing-area', {
	    height: '30em',
		width: ''
	});
	myEditor.render();
</script>

<%@ include file="/WEB-INF/template/header.jsp"%>

<div id="index" class="home">
	<table id="compose-message-table">
	<tr>
	<td id="link-cell">
		<div id="link-panel">
			<a id="inbox-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/inbox.form">OMail Inbox</a>
			<a id="compose-message-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/compose_message.form">Compose Message</a>
			<a id="sent-messages-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/sent_messages.form">Sent Messages</a>
			<a id="settings-link" class="panel-link" href="<openmrs:contextPath/>/module/messaging/settings.form">Settings</a>
		</div>
	</td>
	<td id="compose-message-cell">
		<div id="header-panel" class="boxHeader">
			<table id="header-table">
				<tr>
					<td><span id="to-label">To:</span></td>
					<td class="header-left-column"><textarea id="to-addresses" rows="2"> </textarea></td>
				</tr>
				<tr>
					<td><span id="subject-label">Subject:</span></td>
					<td class="header-left-column"><input id="subject" type="text"/></td>
				</tr>
			</table>
		</div>
		<div id="writing-area-container" class="yui-skin-sam">
			<textarea id="writing-area" name="writing-area"/></textarea>
		</div>
		<div id="buttons-container" class="boxHeader">
			<input type="button" value="Send" id="send-button" onclick="sendMessage()"></input>
			<input type="button" value="Discard" id="discard-button" onclick="clearFields()"></input>
		</div>
	</td>
	</tr>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>
<!--  jQuery includes -->
<link rel="stylesheet" type="text/css" href="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery-ui-1.8.10.custom.css"/>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<openmrs:contextPath/>/moduleResources/messaging/jquery/jquery-ui-1.8.10.custom.min.js"></script>
<style>
	.ui-autocomplete-loading { background: white url('<openmrs:contextPath/>/moduleResources/messaging/images/ajax-loader-circle.gif') right center no-repeat; }
</style>

<!--  DWR includes -->
<openmrs:htmlInclude file="/dwr/engine.js"/>
<openmrs:htmlInclude file="/dwr/util.js"/>
<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingAddressService.js"></script>
<script src="<openmrs:contextPath/>/dwr/interface/DWRModuleMessageService.js"></script>
<script type="text/javascript">
$(function() {
	function split( val ) {
		return val.split( /,\s*/ );
	}
	function extractLast( term ) {
		return split( term ).pop();
	}

	$( "#to-addresses" )
		// don't navigate away from the field on tab when selecting an item
		.bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
					$( this ).data( "autocomplete" ).menu.active ) {
				event.preventDefault();
			}
		})
		.autocomplete({
			source: function( request, response ) {
				DWRMessagingAddressService.autocompleteSearch(extractLast(request.term), response);
			},
			search: function() {
				// custom minLength
				var term = extractLast( this.value );
				if ( term.length < 2 ) {
					return false;
				}
			},
			focus: function() {
				// prevent value inserted on focus
				return false;
			},
			select: function( event, ui ) {
				var terms = split( this.value );
				// remove the current input
				terms.pop();
				// add the selected item
				terms.push( ui.item.value );
				// add placeholder to get the comma-and-space at the end
				terms.push( "" );
				this.value = terms.join( ", " );
				return false;
			}
		});
});

	function sendMessage(){
		myEditor.saveHTML();
	    //The var html will now have the contents of the textarea
	    var html = myEditor.get('element').value, match;
		match = html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);
	    html = match ? match[1] : html;
	    DWRModuleMessageService.sendMessage(html, document.getElementById('to-addresses').value,document.getElementById('subject').value,true,function(response){
			alert("Message sent!");
	    });
	    clearFields();
	}

	function clearFields(){
		myEditor.clearEditorDoc();
	    document.getElementById('to-addresses').value="";
	    document.getElementById('subject').value="";
	}
</script>