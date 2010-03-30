<%@ include file="/WEB-INF/template/include.jsp"%>

<div id="patientMessagesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.messages.box.title" /></div>
<div id="patientMessagesBox" class="box">
	Hello father
</div>
<br/>
<div id="patientAddressesBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.addresses.box.title" /></div>
<div id="patientAddressesBox" class="box">
	<form action="">
		Add an address, fucker
		<select name="addressServiceDropdown" id="serviceDropdown" >
			<c:forEach items="${serviceTitles}" var="title">
				<option value="${title}">${title}</option>
			</c:forEach>
			
		</select>
		<input type="text">enter the address</input>
		<input type="submit" value="Add"/>
	</form>
	WHAT THE FUCK
	<table>
	<tr>
		<th>Service</th>
		<th>Address</th>
		<th>Date Added</th>
	</tr>
	<tr>
		<td>SMS</td>
		<td>+18064702422</td>
		<td>08/14/1989</td>
	</tr>
	
	</table>
</div>
<br/>
<div id="sendAMessageBoxHeader" class="boxHeader"><spring:message code="@MODULE_ID@.send.message.box.title" /></div>
<div id="sendAMessageBox" class="box">
	Hello brother
</div>