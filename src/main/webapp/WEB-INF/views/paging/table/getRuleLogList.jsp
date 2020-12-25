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
	<table id="usingLogList" class="table table-bordered" width="100%" cellspacing="0">
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
					<td><a href="getMemberByAdmin?member_id=${item.member_id}">${item.member_id}</a></td>
					<td>
						<div><b>${item.content}</b></div>
						<c:if test="${item.top_level_name != null}">
							<div>
								대분류 : 
								<a href="${pageContext.request.contextPath}/rule/ruleList/${item.top_level_id}/0/0">
									${item.top_level_name}
								</a>
							</div>
						</c:if>
						<c:if test="${item.middle_level_name != null}">
							<div>
								중분류 : 
								<a href="${pageContext.request.contextPath}/rule/ruleList/${item.top_level_id}/${item.middle_level_id}/0">
									${item.middle_level_name}
								</a>	
							</div>
						</c:if>
						<c:if test="${item.bottom_level_name != null}">
							<div>
								소분류 : 
								<a href="${pageContext.request.contextPath}/rule/ruleList/${item.top_level_id}/${item.middle_level_id}/${item.bottom_level_id}">
									${item.bottom_level_name}
								</a> 
							</div>
						</c:if>
						<c:if test="${item.library_file_name != null}">
							<div>
								라이브러리 : ${item.library_file_name} 
							</div>
						</c:if>
					</td>
					<td>${item.ip_addr}</td>
					<td>${item.time}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<button class="btn btn-secondary" type="button" id="backBtn" style="float:left;" onclick="clickBackBtn();">뒤로 가기</button>
	<button type="button" style="float: right;" class="btn btn-primary btn-icon-split" onclick="fnExcelReport('usingLogList','usingLog');">
		<span class="icon text-white-50"><i class="fas fa-download fa-sm text-white-50"></i></span>
		<span class="text">Excel</span>
	</button>

<script
	src="${pageContext.request.contextPath}/resource/js/table/table.js"></script>
</body>
</html>