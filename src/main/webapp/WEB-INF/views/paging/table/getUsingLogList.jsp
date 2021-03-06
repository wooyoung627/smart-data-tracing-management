<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>
	<meta charset="utf-8">
	<title>Table</title>
</head>

<body>
<input type="hidden" id="title" value="사용 기록 조회">
	<table id="downTable" class="table table-bordered paging-table" width="100%" cellspacing="0">
		<thead>
			<tr>
				<th>no.</th>
				<th>아이디</th>
				<th>사용 내역</th>
				<th>IP 주소</th>
				<th>접속 시간</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${result}" var="item" varStatus="status">
				<tr>
					<td>${item.row_num}</td>
					<td><a href="${pageContext.request.contextPath}/getMemberByAdmin?member_id=${item.member_id}">${item.member_id}</a></td>
					<td>
						<c:choose>
							<c:when test="${fn:contains(item.content, '룰')}">
								<a href="${pageContext.request.contextPath}/getRuleLogList?data=${item.no}&current_page_no=1&count_per_page=10&count_per_list=10&search_word=">${item.content}</a>
							</c:when>
							<c:when test="${fn:contains(item.content, '전사데이터 문장 수정')}">
								<a href="${pageContext.request.contextPath}/goUtterance?data=${item.no}">${item.content}</a>
							</c:when>
							<c:otherwise>
								${item.content}
							</c:otherwise>
						</c:choose>
					</td>
					<td>${item.ip_addr}</td>
					<td>${item.time}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

<script
		src="${pageContext.request.contextPath}/resource/js/paging/table.js"></script>
</body>
</html>
