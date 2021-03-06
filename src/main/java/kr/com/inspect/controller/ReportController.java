package kr.com.inspect.controller;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.com.inspect.dto.Member;
import kr.com.inspect.dto.Metadata;
import kr.com.inspect.dto.Rule;
import kr.com.inspect.dto.RuleLog;
import kr.com.inspect.dto.UsingLog;
import kr.com.inspect.dto.Utterance;
import kr.com.inspect.report.DocxReport;
import kr.com.inspect.report.PrevRuleResult;
import kr.com.inspect.report.XlsxReport;
import kr.com.inspect.service.PostgreService;
import kr.com.inspect.service.RuleService;
import kr.com.inspect.util.ClientInfo;
import kr.com.inspect.util.UsingLogUtil;

/**
 * 리스트 형식 출력 컨트롤러
 * @author Wooyoung Lee
 * @author Yeonhee Kim
 * @version 1.0
 *
 */

@Controller
@PropertySource(value = "classpath:properties/directory.properties")
public class ReportController {

	/**
	 * PostgreSQL 서비스 필드 선언
	 */
	@Autowired
	private PostgreService postgreService;
	
	/**
	 * Metadata 리스트 필드 선언
	 */
	private List<Metadata>  metadata;
	
	/**
	 * 문장 리스트 필드 선언
	 */
	private List<Utterance>  utterances;
	
	/**
	 * Metadata 필드 선언
	 */
	private Metadata meta;

	/**
	 * docx 파일 생성 객체를 필드로 선언
	 */
	@Autowired
	private DocxReport docxReport;

	/**
	 * xlsx 파일 생성 객체를 필드로 선언
	 */
	@Autowired
	private XlsxReport xlsxReport;

	/**
	 * docx 파일이 산출물로 저장될 경로
	 */
	@Value("${report.docx.directory}")
	private String docxPath;
	
	/**
	 * xlsx 파일이 산출물로 저장될 경로
	 */
	@Value("${report.xlsx.directory}")
	private String xlsxPath;
	
	/**
	 * 사용자의 사용 로그 기록을 위한 UsingLogUtil 객체
	 */
	@Autowired
	private UsingLogUtil usingLogUtil;

	/**
	 * 전사규칙에 관한 Service
	 */
	@Autowired
	private RuleService ruleService;
	
	/**
	 * 사용자 접속 정보를 가져오는 객체
	 */
	@Autowired
	private ClientInfo clientInfo;

	/**
	 * 한국어 강의 목록 리스트 파일로 출력
	 * @param response 사용자에게 보내는 응답
	 * @param format 리스트 형식 값
	 * @param data 데이터 타입(전체/한국어 강의/회의 음성/고객 응대/상담 음성)
	 * @throws Exception 예외처리
	 */
	@GetMapping("/metadata/{format}/{data}")
	public void writeMetadata(HttpServletResponse response,
							  @PathVariable String format,
							  @PathVariable String data) throws Exception {
		metadata = postgreService.getMetadataAndProgram(data);
		
		/* 데이터 타입에 맞는 문서 제목 부여 */
		String title = getTitleByDataType(data);

		UsingLog usingLog = new UsingLog();
		
		switch(format) {
			case ("docx"): //docx 파일
				docxReport.writeDocxMetadata(response, docxPath, metadata, "download", title);
				usingLog.setContent(title + ".docx 다운로드");
				//System.out.println();
				break;
			case ("xlsx"): //xlsx 파일
				xlsxReport.writeXlsxMetadata(response, xlsxPath, metadata, "download", title);
				usingLog.setContent(title + ".xlsx 다운로드");
				break;
			default:
				break;
		}
		usingLogUtil.setUsingLog(usingLog);
	}

	/**
	 * utterance 리스트 docx 파일로 출력
	 * @param response 사용자에게 보내는 응답
	 * @param format utterance index 값
	 * @throws Exception 예외처리
	 */
	@GetMapping("/utterance/docx/{format}")
	public void writeUtteranceDocx(HttpServletResponse response,
							  @PathVariable Integer format)throws Exception {
		meta = postgreService.getMetadataAndProgramUsingId(format);
		utterances = postgreService.getUtteranceUsingMetadataId(format);

		docxReport.writeDocxUtterance(response, docxPath, utterances, meta, "download");
		UsingLog usingLog = new UsingLog();
		usingLog.setContent(meta.getTitle() + ".docx 다운로드");
		usingLogUtil.setUsingLog(usingLog);
	}

	/**
	 * utterance 리스트 xlsx 파일로 출력
	 * @param response 사용자에게 보내는 응답
	 * @param model 속성부여
	 * @param format utterance index 값
	 * @throws Exception 예외처리
	 */
	@GetMapping("/utterance/xlsx/{format}")
	public void writeUtteranceXlsx(HttpServletResponse response,
							   Model model,
							   @PathVariable Integer format)throws Exception {
		meta = postgreService.getMetadataAndProgramUsingId(format);
		utterances = postgreService.getUtteranceUsingMetadataId(format);

		xlsxReport.writeXlsxUtterance(response, xlsxPath, utterances, meta, "download");

		UsingLog usingLog = new UsingLog();
		usingLog.setContent(meta.getTitle() + ".xls 다운로드");
		usingLogUtil.setUsingLog(usingLog);
	}

	/**
	 * 한국어 강의목록 파일 mail 전송
	 * @param session 해당 유저의 세션
	 * @param response 사용자에게 보내는 응답
	 * @param format metadata index 값
	 * @param data 데이터 타입(전체/한국어 강의/회의 음성/고객 응대/상담 음성)
	 * @throws Exception 예외 처리
	 */
	@GetMapping("/metadataMail/{format}/{data}")
	public void sendMetadataMail(HttpSession session,
								 HttpServletResponse response,
							  @PathVariable String format,
							  @PathVariable String data) throws Exception {
		
		/* 데이터 타입에 맞는 문서 제목 부여 */
		String title = getTitleByDataType(data);
		
		// 사용자의 이메일 정보를 받아옴
		Member member = (Member)session.getAttribute("member");
		String email = member.getEmail();
		// 파일에 출력할 metadata table
		metadata = postgreService.getMetadataAndProgram(data);

		UsingLog usingLog = new UsingLog();

		switch(format) {
			case ("docx"): //docx 파일 , mail이라는 표시와 email정보를 함께 보냄
				docxReport.writeDocxMetadata(response, docxPath, metadata, "mail"+email, title);
				usingLog.setContent(title + ".docx 메일전송");
				break;
			case ("xlsx"): //xlsx 파일, mail이라는 표시와 email정보를 함께 보냄
				xlsxReport.writeXlsxMetadata(response, xlsxPath, metadata, "mail"+email, title);
				usingLog.setContent(title + ".xlsx 메일전송");
				break;
			default:
				break;
		}

		usingLogUtil.setUsingLog(usingLog);
	}

	/**
	 * 강의 문장 파일 mail 전송
	 * @param session 해당 유저의 세션
	 * @param request 사용자로부터 들어온 요청
	 * @param response 사용자에게 보내는 응답
	 * @throws Exception 예외처리
	 */
	@GetMapping("/utteranceMail")
	@ResponseBody
	public void sendUtteranceMail(HttpSession session,
								  HttpServletRequest request,
								  HttpServletResponse response) throws Exception {
		
		// 사용자의 이메일 정보를 받아옴
		Member member = (Member)session.getAttribute("member");
		String email = member.getEmail();
		int format = Integer.parseInt(request.getParameter("metaId"));
		// 해당 utterance table의 metadata
		meta = postgreService.getMetadataAndProgramUsingId(format);
		// 파일에 출력할 utterance table
		utterances = postgreService.getUtteranceUsingMetadataId(format);

		UsingLog usingLog = new UsingLog();

		switch(request.getParameter("file")) {
			case ("docx"): //docx 파일
				docxReport.writeDocxUtterance(response, docxPath, utterances, meta, "mail"+email);
				usingLog.setContent(meta.getTitle() + ".docx 메일전송");
				break;
			case ("xlsx"): //xlsx 파일
				xlsxReport.writeXlsxUtterance(response, xlsxPath, utterances, meta, "mail"+email);
				usingLog.setContent(meta.getTitle() + ".xlsx 메일전송");
				break;
			default:
				break;
		}

		usingLogUtil.setUsingLog(usingLog);
	}

	/**
	 * 데이터 타입을 입력하면 데이터 타입에 맞는 문서 제목을 리턴함
	 * @param data 데이터 타입(전체/한국어 강의/회의 음성/고객 응대/상담 음성)
	 * @return 데이터 타입에 맞는 제목을 리턴함
	 */
	public String getTitleByDataType(String data) {
		String title = "전사 데이터 목록";
		if(data.equals("all")) {
			title += "(전체)";
		}else if(data.equals("korean_lecture")) {
			title += "(한국어 강의)";
		}else if(data.equals("meeting_audio")) {
			title += "(회의 음성)";
		}else if(data.equals("customer_reception")) {
			title += "(고객 응대)";
		}else if(data.equals("counsel_audio")) {
			title += "(상담 음성)";
		}
		return title;
	}
	
	/**
	 * 룰 기록 조회에서 이전에 다운받았던 룰 보고서 파일을 다운 받음
	 * @param response HttpServletResponse
	 * @param time 파일명(이전에 다운받은 시간)
	 */
	@GetMapping("/downloadPrevRuleReport")
	@ResponseBody
	public void downloadPrevRuleReport(HttpServletResponse response, String time) {
		time = time.replace(" ", "_");
		time = time.replace(":", "_");
		String ruleReportPath = docxPath + "ruleReport" + File.separator;
		docxReport.downloadPrevRuleReport(response, ruleReportPath, time);
	}

	/**
	 * 룰 결과를 워드파일로 다운
	 * @param session HttpSession 객체
	 * @param response HttpServletResponse 객체
	 * @param hiddenRule 룰 소분류 아이디를 담고 있는 배열
	 * @param time 이전에 룰 보고서를 다운받았던 시간(서버 스토리지에 저장된 룰 보고서 파일명)
	 */
	@GetMapping("/resultRuleDocx")
	@ResponseBody
	public void resultRuleWord(HttpSession session, HttpServletResponse response, int[] hiddenRule, String time) {
		Arrays.sort(hiddenRule);

		// 사용자의 이메일 정보를 받아옴
		Member member = (Member)session.getAttribute("member");
		String name = member.getName();
		List<Rule> ruleList = new ArrayList<>();
		
		if(time == null) {
			String usingLogContent = "룰 결과 보고서 다운로드 - 총 "+hiddenRule.length+"개";
			final int USING_LOG_NO = usingLogUtil.insertUsingLog(usingLogContent);
			time = clientInfo.getTime();
			RuleLog ruleLog = new RuleLog();
			ruleLog.setContent(usingLogContent);
			ruleLog.setUsing_log_no(USING_LOG_NO);
			ruleLog.setTime(time);
			usingLogUtil.setUsingLog(ruleLog);
			for(int i=0; i<hiddenRule.length; i++){
				Rule rule = ruleService.getRuleBottomLevel(hiddenRule[i]);
				RuleLog ruleLogDetail = new RuleLog();
				ruleLogDetail.setUsing_log_no(USING_LOG_NO);
				ruleLogDetail.setContent("룰 결과 보고서 다운로드");
				ruleLogDetail.setRule(rule);
				usingLogUtil.insertRuleLogDetail(ruleLogDetail);
				ruleList.add(rule);
			}
		}else {
			String editTime = time.replace(" ", "_");
			editTime = editTime.replace(":", "_");
			PrevRuleResult prevRuleResult = new PrevRuleResult();
			ruleList = prevRuleResult.unZip(editTime, hiddenRule);
		}
		String ruleReportPath = docxPath + "ruleReport" + File.separator;
		docxReport.resultRuleDocx(response, ruleList, ruleReportPath, name, time);
	}

	/**
	 * 엑셀 다운 로그
	 * @param title 엑셀 다운로드한 제목
	 */
	@ResponseBody
	@PostMapping("/downloadExcel")
	public void approval(String title){
		UsingLog usingLog = new UsingLog();
		usingLog.setContent(title + ".xls 다운로드");
		usingLogUtil.setUsingLog(usingLog);
	}
}
