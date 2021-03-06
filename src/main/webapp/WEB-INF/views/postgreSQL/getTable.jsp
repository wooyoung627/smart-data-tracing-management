<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>
	<script src="http://code.jquery.com/jquery-1.4.4.js"></script>

	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="">
	<meta name="author" content="">

	<title>SDTM</title>

	<!-- Custom fonts for this template-->
	<link href="${pageContext.request.contextPath}/resource/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
	<link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

	<!-- Custom styles for this template-->
	<link href="${pageContext.request.contextPath}/resource/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<!-- Page Wrapper -->
<div id="wrapper">

	<!-- 사이드바 include-->
	<%@ include file="../include/sidebar.jsp"%>

	<!-- Content Wrapper -->
	<div id="content-wrapper" class="d-flex flex-column">

		<!-- Main Content -->
		<!-- 이 부분만 바꿔주면 됩니다 -->
		<div id="content">
			<!-- 툴바 include -->
			<%@ include file="../include/toolbar.jsp"%>
			<!-- Begin Page Content -->
			<div class="container-fluid">

				<!-- Page Heading -->
				<div class="d-sm-flex align-items-center justify-content-between mb-4">
					<h1 class="h3 mb-2 text-gray-800">
						<b>전사 데이터 목록</b>
						<span style="font-size:18px;">(${selectedData} ${totalCount}개)</span>
					</h1>
					<div>
						<!-- 파일 다운로드 버튼 -->
						<a href="${pageContext.request.contextPath}/metadata/docx/${data}" class="d-sm-inline-block btn btn-sm btn-primary shadow-sm"><i
								class="fas fa-download fa-sm text-white-50"></i> Word</a>
						<a href="${pageContext.request.contextPath}/metadata/xlsx/${data}" class="d-sm-inline-block btn btn-sm btn-primary shadow-sm"><i
								class="fas fa-download fa-sm text-white-50"></i> Excel</a>
						<div class="my-2"></div>
						<!-- 파일 전송 버튼 -->
						<div>
							<ul class="navbar-nav ml-auto">
								<li class="nav-item dropdown no-arrow">
									<a class="nav-link dropdown-toggle btn btn-primary shadow-sm btn-sm " href="#" id="fileSend" style="width: 105pt"
									   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i
											class="fas fa-paper-plane fa-sm text-white-50"></i>&nbsp;&nbsp;파일 전송</a>
									<!-- Dropdown - User Information -->
									<div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
										 aria-labelledby="fileSend">
										<h5 class="dropdown-header" style="color: black;font-size: 1.5em">
											<i class="fas fa-at"></i>&nbsp;&nbsp;E-Mail
										</h5>
										<div class="my-2"></div>
										<a class="dropdown-item btn" onclick="send('mail','metadataMail/docx');">
											<h6>- Word</h6>
										</a>
										<a class="dropdown-item btn" onclick="send('mail','metadataMail/xlsx');">
											<h6>- Excel</h6>
										</a>
										<a class="dropdown-item text-center small text-gray-500">${member.email}(으)로 발송됩니다.</a>
									</div>
								</li>
							</ul>
						</div>
					</div>
				</div>

				<!-- Page Body -->
				<div class="card shadow mb-4">
					<div class="card-body"><br/>
						<%@ include file="/WEB-INF/views/paging/template.jsp"%>
					</div>
				</div>

			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- End of Main Content -->

		<!-- footer include-->
		<%@ include file="../include/footer.jsp"%>

	</div>
	<!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<!-- Scroll to Top Button-->
<a class="scroll-to-top rounded" href="#page-top">
	<i class="fas fa-angle-up"></i>
</a>
<script src="${pageContext.request.contextPath}/resource/js/paging/table.js"></script>
<script>
function send(type, fileurl){
	var dataType = document.getElementById("show_data_type").value;
	var url = '${pageContext.request.contextPath}/' + fileurl + '/'+ dataType;

	$.ajax({
		type:"GET",
		url: url,

		success:function (){
			if(type == 'mail')
				alert("메일이 성공적으로 전송되었습니다.");
		},
		error: function (){
			alert("에러");
		}
	})

	if(type == 'mail')
		alert("메일이 전송됩니다.");
}
</script>

<!-- Bootstrap core JavaScript-->
<script src="${pageContext.request.contextPath}/resource/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/resource/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Core plugin JavaScript-->
<script src="${pageContext.request.contextPath}/resource/vendor/jquery-easing/jquery.easing.min.js"></script>

<!-- Custom scripts for all pages-->
<script src="${pageContext.request.contextPath}/resource/js/sb-admin-2.min.js"></script>

<!-- Page level plugins -->
<script src="${pageContext.request.contextPath}/resource/vendor/chart.js/Chart.min.js"></script>

<!-- Page level custom scripts -->
<script src="${pageContext.request.contextPath}/resource/js/demo/chart-area-demo.js"></script>
<script src="${pageContext.request.contextPath}/resource/js/demo/chart-pie-demo.js"></script>
</body>

</html>
