<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<!-- Sidebar -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- Sidebar - Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="${pageContext.request.contextPath}/main">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-laugh-wink"></i>
        </div>
        <div class="sidebar-brand-text mx-3">SDTM</div>
    </a>

    <!-- Divider -->
    <hr class="sidebar-divider my-0">

    <!-- Nav Item - Dashboard -->
    <sec:authorize access="hasRole('ROLE_VIEW')">
	    <li class="nav-item active">
	        <a class="nav-link" href="${pageContext.request.contextPath}/main">
	            <i class="fas fa-fw fa-tachometer-alt"></i>
	            <span>대시보드</span></a>
	    </li>
	
	    <!-- Divider -->
	    <hr class="sidebar-divider">
	
	    <!-- Heading -->
	    <div class="sidebar-heading">
	        메뉴
	    </div>
		
	    <!-- 데이터 관리 메뉴 -->
	    <li class="nav-item">
	        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#dataManage" aria-expanded="true" aria-controls="dataManage">
	            <i class="fas fa-fw fa-folder"></i>
	            <span>데이터 관리</span>
	        </a>
	        <div id="dataManage" class="collapse" aria-labelledby="headingPages" data-parent="#accordionSidebar">
	            <div class="bg-white py-2 collapse-inner rounded">
	                <h6 class="collapse-header">데이터 관리</h6>
	                <sec:authorize access="hasRole('ROLE_INPUT')">
	                	<a class="collapse-item" href="${pageContext.request.contextPath}/insertIntoPostgre">데이터 입력</a>
	                </sec:authorize>
	                <a class="collapse-item" href="${pageContext.request.contextPath}/getJsonLog?&current_page_no=1&count_per_page=10&count_per_list=10&search_word=">JSON 파일 관리</a>
	                <a class="collapse-item" href="${pageContext.request.contextPath}/getMetadataAndProgram?data=all&current_page_no=1&count_per_page=10&count_per_list=10&search_word=">전사 데이터 목록</a>
	            </div>
	        </div>
	    </li>
    </sec:authorize>
    
	<!-- 룰 관리 메뉴 -->
   <sec:authorize access="hasRole('ROLE_ADMIN')">
	   <li class="nav-item">
	       <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#ruleManage" aria-expanded="true" aria-controls="ruleManage">
	           <i class="fas fa-clipboard-list"></i>
	           <span>&nbsp;&nbsp;룰 관리</span>
	       </a>
	       <div id="ruleManage" class="collapse" aria-labelledby="headingPages" data-parent="#accordionSidebar">
	           <div class="bg-white py-2 collapse-inner rounded">
	               <h6 class="collapse-header">룰 관리</h6>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/rule/registerRule">룰 등록</a>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/rule/ruleList/0/0/0">룰 목록</a>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/rule/runRule">룰 실행</a>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/getRuleLogList?data=0&current_page_no=1&count_per_page=10&count_per_list=10&search_word=&log_type=all&member_id=&using_list=&ip_addr=&access_time=">룰 기록 조회</a>
	           </div>
	       </div>
	   </li>
   </sec:authorize>

   <!-- 사용자 관리 메뉴 -->
   <sec:authorize access="hasRole('ROLE_ADMIN')">
	   <li class="nav-item">
	       <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#memberManage" aria-expanded="true" aria-controls="memberManage">
	           <i class="fas fa-cog"></i>
	           <span>시스템 관리</span>
	       </a>
	       <div id="memberManage" class="collapse" aria-labelledby="headingPages" data-parent="#accordionSidebar">
	           <div class="bg-white py-2 collapse-inner rounded">
	               <h6 class="collapse-header">시스템 관리</h6>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/getMemberListByAdmin?data=ALL&current_page_no=1&count_per_page=10&count_per_list=10&search_word=&approval=">사용자 관리</a>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/getUsingLogList?data=&current_page_no=1&count_per_page=10&count_per_list=10&search_word=&log_type=all&member_id=&using_list=&ip_addr=&access_time=">사용 기록 조회</a>
	               <a class="collapse-item" href="${pageContext.request.contextPath}/runSQLPage">SQL 실행</a>
	           </div>
	       </div>
	   </li>
   </sec:authorize>

    <!-- Divider -->
    <hr class="sidebar-divider d-none d-md-block">

    <!-- Sidebar Toggler (Sidebar) -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>
</ul>
<!-- End of Sidebar -->