<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<script src="<openmrs:contextPath/>/dwr/interface/DWRMessagingService.js"></script>

<script type="text/javascript">
	function updatePassword(text) {
		DWRMessagingService.requiresPassword(text, 
				function(val) {
					document.getElementById("password").style.display = val? "inline":"none";
					document.getElementById("pwdLabel").style.display = val? "inline":"none";
				});
	}
</script>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Manage Your Messaging Addresses</h2><br/>

<div id="patientAddressesBoxHeader" class="boxHeader"><spring:message code="messaging.addresses.box.title" /></div>
<div id="patientAddressesBox" class="box">
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
			<c:forEach var="address" items="${addresses}">
				<tr>
					<td><msg:addressType address="${address}"/></td>
					<td>${address.address}</td>
					<td><openmrs:formatDate date="${address.dateCreated}" /></td>
					<td><c:if test='${address.preferred == true}'>*</c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br/>
	<form method="post" action="<openmrs:contextPath/>/module/messaging/addAddress.form">
		Add a messaging address 
		<input type="hidden" name="returnUrl" value="/module/messaging/admin/manageAddresses.form"/>
		<select id="selectAddressType" name="address_type" onchange="updatePassword(this.options[this.selectedIndex].text)">
			<c:forEach var="type" items="${addressTypes}">
				<option value="${type}">${type}</option>
			</c:forEach>
		</select>
		<label for="address">Address:</label> 
		<input type="text" name="address" id="address"></input>
		<label for="password" id="pwdLabel">Password:</label> 
		<input type="password" name="password" id="password"></input> 
		<input type="checkbox" name="preferred">preferred</input>
		<input type="submit"value="Add" />
	</form>
</div>