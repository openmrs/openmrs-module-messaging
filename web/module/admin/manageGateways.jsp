<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<h2>Manage Messaging Gateways</h2><br/>
<div id="phonesBoxHeader" class="boxHeader">Phones and Modems</div>
<div id="phonesBox" class="box">
<c:choose>
<c:when test="${fn:length(modems) > 0}">
<table id="phonesTable">
	<thead>
		<tr>
			<th>Port</th>
			<th>Phone Number</th>
			<th>Model</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="modem" items="${modems}">
			<tr>
				<td>${modem.port}</td>
				<td>${modem.number}</td>
				<td>${modem.model}</td>
				<td>${modem.status}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</c:when>
<c:otherwise>
There were no phones detected
</c:otherwise>
</c:choose>
<br/>
<br/>
<form method="post" action="<openmrs:contextPath/>/module/messaging/detectModems.form">
	<input type="submit" value="Detect" />
</form>
</div>

<br/>
<div id="twitterBoxHeader" class="boxHeader">Twitter</div>
<div id="twitterBox" class="box">
Twitter username: ${twitterUsername}<br/><br/>
Change the Twitter login:
<form method="post" action="<openmrs:contextPath/>/module/messaging/changeTwitterCreds.form">
	Username:  <input type="text" name="username"/><br/>
	Password:  <input type="password" name="password1"/><br/>
	Confirm Password:  <input type="password" name="password2"/><br/>
	<input type="submit" value="Save Changes" />
</form>
<br/><br/>
Twitter gateway status: ${twitterStatus}
</div>

<br/>
<div id="googleVoiceBoxHeader" class="boxHeader">Google Voice</div>
<div id="googleVoiceBox" class="box">
Google Voice username: ${googleVoiceUsername}<br/><br/>
Change the Google Voice login:
<form method="post" action="<openmrs:contextPath/>/module/messaging/changeGoogleVoiceCreds.form">
	Username:  <input type="text" name="username"/><br/>
	Password:  <input type="password" name="password1"/><br/>
	Confirm Password:  <input type="password" name="password2"/><br/>
	<input type="submit" value="Save Changes" />
</form>
<br/><br/>
Google Voice gateway status: ${googleVoiceStatus}
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>