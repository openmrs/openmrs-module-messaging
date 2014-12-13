<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2> <spring:message code="messaging.sendMessage" /></h2>

</br>
<form class="box" method="post" action="<openmrs:contextPath/>/module/messaging/admin/sendMessage.form">
	<input type="hidden" name="sender" value="${user.personId}"/>
	<select name="fromAddress">
			<c:forEach var="from" items="${fromAddresses}">
				<option value="${from.address}"/>${from.address}</option>
			</c:forEach>
	</select>
	<br/>
	<input type="text" name="toAddress"></input><br/><br/>
	<textarea rows="6" cols="40" name="content"></textarea><br/><br/>
	<input type="submit" value="<spring:message code="messaging.send" />"/>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>