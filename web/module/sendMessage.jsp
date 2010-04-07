<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Send a Message</h2>

</br>
<form method="post" class="box">
	<select id="selectService" name="service">
			<option value="SMS">SMS</option>
	</select>
	<input type="text" name="address"></input><br/><br/>
	<textarea rows="6" cols="40" name="content"></textarea><br/><br/>
	<input type="submit" value="Send" />
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>