<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#myTable').dataTable();
	} );
</script>


<div id="patientMessagesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.messages.box.title" /></div>
<div id="patientMessagesBox" class="box">
</div>
<br/>
<div id="patientAddressesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.addresses.box.title" /></div>
<div id="patientAddressesBox" class="box">
	<form action="">
		Add an address
		<select id="selectService" name="serviceSelector">
			<c:forEach var="title" items="${ model.serviceTitles }">
				<option value="${title}">${title}</option>
			</c:forEach>
		</select>
		<input type="text">enter the address</input>
		<input type="submit" value="Add"/>
	</form>

	<table>
	<thead>
		<th>Service</th>
		<th>Address</th>
		<th>Date Added</th>
	</thead>
	<tbody>
		<td>SMS</td>
		<td>+18064702422</td>
		<td>08/14/1989</td>
	</tbody>
	
	</table>
</div>
<br/>
<div id="sendAMessageBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.send.message.box.title" /></div>
<div id="sendAMessageBox" class="box">
</div>