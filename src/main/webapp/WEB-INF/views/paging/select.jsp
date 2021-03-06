<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>
	<meta charset="utf-8">
	<title>Table</title>
</head>

<body>
	<c:choose>
		<c:when test="${requestUrl == 'getMetadataAndProgram'}">
		  	<%@ include file="/WEB-INF/views/paging/select/getMetadataAndProgram.jsp"%>
	  	</c:when>
	  	<c:when test="${requestUrl == 'getMemberListByAdmin'}">
	  		<%@ include file="/WEB-INF/views/paging/select/getMemberListRole.jsp"%>
	  		<%@ include file="/WEB-INF/views/paging/select/getMemberListApproval.jsp"%>
	  	</c:when>
	  	<c:when test="${requestUrl == 'getUsingLogList' || requestUrl == 'getRuleLogList'}">
	  		<%@ include file="/WEB-INF/views/paging/select/getUsingRuleLogList.jsp"%>
	  	</c:when>
  	</c:choose>
</body>

</html>
