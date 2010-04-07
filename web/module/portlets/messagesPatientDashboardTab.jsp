<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>
<openmrs:htmlInclude file="/moduleResources/messaging/tablesorter/jquery.tablesorter.min.js" />

<script type="text/javascript">
	$j(document).ready(function() {
		$j("#messageTable").tablesorter();
		$j("#addressesTable").tablesorter(); 
	});
</script>


<div id="patientMessagesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.messages.box.title" /></div>
<div id="patientMessagesBox" class="box">

<table id="messageTable">
	<thead>
		<tr>
			<th>To</th>
			<th>From</th>
			<th>Content</th>
			<th>Date Sent</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="message" items="${ model.messages }">
			<tr>
				<td>${message.destination}</td>
				<td>${message.origin}</td>
				<td>${message.content}</td>
				<td><openmrs:formatDate date="${message.dateSent}" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

</div>
<br />
<div id="patientAddressesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.addresses.box.title" /></div>
<div id="patientAddressesBox" class="box">

	<form method="post" action="<openmrs:contextPath/>/module/messaging/addAddress.form">
		Add an address 
		<input type="hidden" name="patient_id" value="${patient.patientId}"/>
		<input type="hidden" name="returnUrl" value="/patientDashboard.form?patientId=${patient.patientId}"/>
		<select id="selectService" name="service">
			<c:forEach var="title" items="${ model.serviceTitles }">
				<option value="${title}">${title}</option>
			</c:forEach>
		</select> 
		<input type="text" name="address"></input> 
		<input type="submit"value="Add" />
	</form>
	
	<table id="addressesTable">
		<thead>
			<tr>
				<th>Service</th>
				<th>Address</th>
				<th>Date Created</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="address" items="${ model.messagingAddresses }">
				<tr>
					<td>unknown</td>
					<td>${address.address}</td>
					<td><openmrs:formatDate date="${address.dateCreated}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	
	</table>
</div>
<br />
<div id="sendAMessageBoxHeader" class="boxHeader"><spring:message
	code="@MODULE_ID@.send.message.box.title" /></div>
<div id="sendAMessageBox" class="box"></div>