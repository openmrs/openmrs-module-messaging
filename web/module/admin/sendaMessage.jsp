<%@ include file="/WEB-INF/view/module/messaging/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Send a Message</h2>

</br>
<form method="post" class="box">
	<select id="gateway" name="gateway">
			<c:forEach var="service" items="${services}">
				<option value="${service}"/>${service}</option>
			</c:forEach>
	</select>
	<input type="text" name="address"></input><br/><br/>
	<textarea rows="6" cols="40" name="content"></textarea><br/><br/>
	<input type="submit" value="Send" />
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>