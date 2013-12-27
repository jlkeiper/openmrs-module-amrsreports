<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Drugs" otherwise="/login.htm" redirect="/module/amrsreports/regimen.list"/>

<script>
    $j(document).ready(function(){
        toggleRowVisibilityForClass('regimenTable', 'voided');
    });
</script>

<%@ include file="localHeader.jsp" %>

<h2>MOH Facilities</h2>

<a href="regimen.form">Add a new regimen</a>

<br /> <br />

<div class="boxHeader">
	<span style="float: right">
		<a href="#" id="showRetired" onClick="return toggleRowVisibilityForClass('regimenTable', 'voided');"><spring:message code="general.toggle.retired"/></a>
	</span>
    <b>All MOH Facilities</b>
</div>
<div class="box">
    <table cellpadding="2" cellspacing="0" id="regimenTable" width="98%">
        <tr>
            <th> <spring:message code="general.name" /> </th>
            <th> <spring:message code="general.description" /> </th>
            <th> Regimen Drugs </th>

        </tr>
        <c:forEach var="regimen" items="${regimens}" varStatus="status">
            <tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"} ${regimen.retired ? "voided" : ""}'>
                <td valign="top" style="white-space: nowrap"><a href="regimen.form?id=${regimen.id}">${regimen.name}</a></td>
                <td valign="top">${regimen.description}</td>
                <td valign="top">${regimen.regimenDrugs}</td>
            </tr>
        </c:forEach>
    </table>
</div>

<br />

<%@ include file="/WEB-INF/template/footer.jsp" %>
