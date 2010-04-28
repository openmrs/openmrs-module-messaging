<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<!-- 
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>
<openmrs:htmlInclude file="/moduleResources/messaging/tablesorter/jquery.tablesorter.min.js" />

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingService.js"></script>

<script type="text/javascript">
	$j(document).ready(function() {
		$j("#messageTable").tablesorter();
		$j("#addressesTable").tablesorter(); 
	});
</script>

<script type="text/javascript">
	function updateAddresses() {
	  var fromAddress = dwr.util.getValue("fromAddress");
	  dwr.util.addOptions("toAddress",DWRMessagingService.getCompatibleAddresses(fromAddress,$j(model.patient.patientId));
	}
</script>
 -->

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

	<form method="post" action="<openmrs:contextPath/>/module/messaging/addAddress.form">
		Add a contact point
		<input type="hidden" name="returnUrl" value="/patientDashboard.form?patientId=${patient.patientId}"/>
		<input type="hidden" name="person" value="${model.patientId}"/>
		<select id="selectAddressType" name="address_type">
			<c:forEach var="type" items="${model.address_types}">
				<option value="${type}">${type}</option>
			</c:forEach>
		</select>
		<input type="text" name="address" id="address"></input>
		<input type="checkbox" name="preferred">preferred</input>
		<input type="submit"value="Add" />
	</form>
	
	<table id="addressesTable">
		<thead>
			<tr>
				<th>Type</th>
				<th>Address</th>
				<th>Date Created</th>
				<th>Preferred</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="address" items="${model.messagingAddresses}">
				<tr>
					<td><msg:addressType address="${address}"/></td>
					<td>${address.address}</td>
					<td><openmrs:formatDate date="${address.dateCreated}" /></td>
					<td><c:if test='${address.preferred == true}'>*</c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<br />
<div id="sendAMessageBoxHeader" class="boxHeader"><spring:message code="messaging.send.message.box.title" /></div>
<div id="sendAMessageBox" class="box">
	<form method="post" action="<openmrs:contextPath/>/module/messaging/sendMessage.form">
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