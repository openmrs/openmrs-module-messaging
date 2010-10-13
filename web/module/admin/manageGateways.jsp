<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<h2>Manage Messaging Gateways</h2><br/>

<div id="nuntiumBoxHeader" class="boxHeader">Nuntium</div>
<div id="nuntiumBox" class="box" style="padding:4px">
<p>
Nuntium is an open source and free platform -developed by InSTEDD- that allows applications to send and receive all type of messages. <a href="http://code.google.com/p/nuntium/" target="_blank">Learn more...</a><br/>
</p>

<form method="post" action="<openmrs:contextPath/>/module/messaging/changeNuntiumCreds.form">
<p>
Enabled:<br/>
Yes <input type="radio" name="enabled" value="yes" ${nuntiumEnabledChecked} onclick="showNuntiumEnabledBox()"/>
No <input type="radio" name="enabled" value="no" ${nuntiumDisabledChecked} onclick="hideNuntiumEnabledBox()" />
</p>
<div id="nuntiumEnabledBox" <% if (!((Boolean)request.getAttribute("nuntiumEnabled"))) {%> style="display:none" <%}%>>
	<div style="margin-left:20px">
		<p>
		URL:<br/>
		<input type="text" name="url" value="${nuntiumUrl}" size="60" />
		</p>
		<p>
		Account:<br/>
		<input type="text" name="account" value="${nuntiumAccount}" size="20"/>
		</p>
		<p>
		Application:<br/>
		<input type="text" name="application" value="${nuntiumApplication}" size="20"/>
		</p>
		<p>
		Password:<br/>
		<input type="password" name="password" size="20"/>
		</p>
	</div>
	<p>
	Configure your Nuntium Application interface to be an HTTP POST callback to:
	<pre style="margin-left:20px">${nuntiumCallbackUrl}</pre>
	with the given user and password:
	</p>
	<div style="margin-left:20px">
		<p>
		Callback user:<br/>
		<input type="text" name="callbackUser" value="${nuntiumCallbackUser}" size="20"/>
		</p>
		<p>
		Callback password:<br/>
		<input type="password" name="callbackPassword" size="20"/>
		</p>
	</div>
</div>
<input type="submit" value="Save Changes" />
</form>
</div>

<br/>
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

<script>
function showNuntiumEnabledBox() {
	document.getElementById('nuntiumEnabledBox').style.display = '';
}
function hideNuntiumEnabledBox() {
	document.getElementById('nuntiumEnabledBox').style.display = 'none';
}
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>