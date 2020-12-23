package kr.com.inspect.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.com.inspect.dto.Member;
import kr.com.inspect.dto.Rule;
import kr.com.inspect.dto.Utterance;

import kr.com.inspect.service.RuleService;
import org.apache.lucene.util.packed.DirectMonotonicReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import kr.com.inspect.dto.Metadata;
import kr.com.inspect.report.DocxReport;
import kr.com.inspect.report.XlsxReport;
import kr.com.inspect.sender.SendReport;
import kr.com.inspect.service.PostgreService;

/**
 * 리스트 형식 출력 컨트롤러
 * @author Woo Young
 * @author Yeonhee Kim
 * @version 1.0
 *
 */

@Controller
@PropertySource(value = "classpath:properties/directory.properties")
public class ReportController {
	/* sms 전송확인을 위해 임시로 선언 */
	@Autowired
	SendReport sendReport;
	
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
	 * hwp 파일이 산출물로 저장될 경로
	 */
	@Value("${report.hwp.directory}")
	private String hwpPath;
	
	/**
	 * pptx 파일이 산출물로 저장될 경로
	 */
	@Value("${report.pptx.directory}")
	private String pptxPath;

	/**
	 * 전사규칙에 관한 Service
	 */
	@Autowired
	private RuleService ruleService;
	
	/**
	 * 한국어 강의 목록 리스트 파일로 출력
	 * @param response 사용자에게 보내는 응답
	 * @param format 리스트 형식 값
	 * @throws Exception 예외처리
	 */
	@GetMapping("/metadata/{format}/{data}")
	public void writeMetadata(HttpServletResponse response,
							  @PathVariable String format,
							  @PathVariable String data) throws Exception {
		metadata = postgreService.getMetadataAndProgram(data);
		
		/* 데이터 타입에 맞는 문서 제목 부여 */
		String title = getTitleByDataType(data);
		
		switch(format) {
			case ("hwp"): //한글 파일
				// hwpReport.writeHwp(hwpPath, list);
				break;
			case ("docx"): //docx 파일
				docxReport.writeDocxMetadata(response, docxPath, metadata, "download", title);
				//System.out.println();
				break;
			case ("xlsx"): //xlsx 파일
				xlsxReport.writeXlsxMetadata(response, xlsxPath, metadata, "download", title);
				break;
			case ("pptx"): //pptx 파일 
				// pptxReport.writePptx(pptxPath, list);
				break;
			default:
				break;
		}
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
	}

	/**
	 * 한국어 강의목록 파일 mail 전송
	 * @param session 해당 유저의 세션
	 * @param response 사용자에게 보내는 응답
	 * @param format metadata index 값
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
		switch(format) {
			case ("docx"): //docx 파일 , mail이라는 표시와 email정보를 함께 보냄
				docxReport.writeDocxMetadata(response, docxPath, metadata, "mail"+email, title);
				break;
			case ("xlsx"): //xlsx 파일, mail이라는 표시와 email정보를 함께 보냄
				//System.out.println("xlsx파일 mail");
				xlsxReport.writeXlsxMetadata(response, xlsxPath, metadata, "mail"+email, title);
				break;
			default:
				break;
		}
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

		switch(request.getParameter("file")) {
			case ("docx"): //docx 파일
				//System.out.println("docx");
				docxReport.writeDocxUtterance(response, docxPath, utterances, meta, "mail"+email);
				break;
			case ("xlsx"): //xlsx 파일
				//System.out.println("xlsxl");
				xlsxReport.writeXlsxUtterance(response, xlsxPath, utterances, meta, "mail"+email);
				break;
			default:
				break;
		}
	}

	/**
	 * 한국어 강의 목록 파일 sms 전송
	 * @param session 해당 유저의 세션
	 * @param response 사용자에게 보내는 응답
	 * @param format metadata index 값
	 * @throws Exception 예외 처리
	 */
	@GetMapping("/metadataSMS/{format}/{data}")
	public void sendMetadataSMS(HttpSession session,
								 HttpServletResponse response,
								 @PathVariable String format,
								 @PathVariable String data) throws Exception {
		
		/* 데이터 타입에 맞는 문서 제목 부여 */
		String title = getTitleByDataType(data);
		
		// 사용자의 phone 정보를 받아옴
		Member member = (Member)session.getAttribute("member");
		String phone = member.getPhone();
		// 파일에 출력할 metadata table
		metadata = postgreService.getMetadataAndProgram(data);

		switch(format) {
			case ("docx"): //docx 파일 , sms이라는 표시와 phone정보를 함께 보냄
				//System.out.println(phone+"docx");
				sendReport.sendSMS(null, null, phone);
//				docxReport.writeDocxMetadata(response, docxPath, metadata, "sms"+phone, title);
				break;
			case ("xlsx"): //xlsx 파일, sms이라는 표시와 phone정보를 함께 보냄
				//System.out.println(phone);
//				xlsxReport.writeXlsxMetadata(response, xlsxPath, metadata, "sms"+phone, title);
				break;
			default:
				break;
		}
	}

	/**
	 * 강의 문장 파일 sms 전송
	 * @param session 해당 유저의 세션
	 * @param request 사용자로부터 들어온 요청
	 * @param response 사용자에게 보내는 응답
	 * @throws Exception 예외처리
	 */
	@GetMapping("/utteranceSMS")
	@ResponseBody
	public void sendUtteranceSMS(HttpSession session,
								  HttpServletRequest request,
								  HttpServletResponse response) throws Exception {
		// 사용자의 phone 정보를 받아옴
		Member member = (Member)session.getAttribute("member");
		String phone = member.getPhone();
		int format = Integer.parseInt(request.getParameter("metaId"));
		// 해당 utterance table의 metadata
		meta = postgreService.getMetadataAndProgramUsingId(format);
		// 파일에 출력할 utterance table
		utterances = postgreService.getUtteranceUsingMetadataId(format);

		switch(request.getParameter("file")) {
			case ("docx"): //docx 파일
				//System.out.println(phone + "docx");
//				sendReport.sendSMS(null, null, phone);
//				docxReport.writeDocxUtterance(response, docxPath, utterances, meta, "sms"+phone);
				break;
			case ("xlsx"): //xlsx 파일
				//System.out.println(phone + "xlsxl");
//				xlsxReport.writeXlsxUtterance(response, xlsxPath, utterances, meta, "sms"+phone);
				break;
			default:
				break;
		}
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
	 * 룰 결과를 워드파일로 다운
	 * @param response
	 * @param bottom_level_id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/resultRuleDocx", method = RequestMethod.GET)
	public void resultRuleWord(HttpServletResponse response, Integer bottom_level_id) throws Exception {
		System.out.println("start");
		Rule rule = ruleService.getRuleBottomLevel(bottom_level_id);
		docxReport.resultRuleDocx(response, rule, docxPath);
		System.out.println("end");

//		Map<String, Object> map;
//		String str = "";
//		int flag = 0;
//		for(int i=0; i<100; i++){
//			if(rule.getResult().charAt(i)=='{'){
//				flag = 1;
//			}
//			if(flag == 1){
//				str += rule.getResult().charAt(i);
//			}
//			if(rule.getResult().charAt(i)=='}'){
//				map = new ObjectMapper().readValue(str, new TypeReference<Map<String, Object>>() { });
//				flag=0;
//				System.out.println("map: " + map);
//				System.out.println("str: " + str);
//			}
//		}
	}
}
