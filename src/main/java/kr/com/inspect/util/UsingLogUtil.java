package kr.com.inspect.util;

import kr.com.inspect.dto.UtteranceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.com.inspect.dao.MemberDao;
import kr.com.inspect.dao.RuleDao;
import kr.com.inspect.dto.RuleLog;
import kr.com.inspect.dto.UsingLog;

/**
 * 사용 로그에 ip, 접속 시간, 아이디, 로그 내용을 세팅함
 * @author Yeonhee Kim
 * @version 1.0
 */
@Component
public class UsingLogUtil {
	
	/**
	 * 클라이언트 정보 필드
	 */
	@Autowired
	private ClientInfo clientInfo;
	
	/**
	 * MemberDao Member DAO 필드 선언
	 */
	@Autowired
	private MemberDao memberDao;
	
	/**
	 * RuleDao Rule DAO 필드 선언
	 */
	@Autowired
	private RuleDao ruleDao;
	
	/**
	 * 사용 로그에 ip, 접속 시간, 아이디, 로그 내용을 세팅하고 DB에 등록함
	 * @param usingLog 사용 로그 객체
	 */
	public void setUsingLog(UsingLog usingLog){
		if(usingLog.getIp_addr() == null) {
			usingLog.setIp_addr(clientInfo.getIpAddr());
		}
		if(usingLog.getTime() == null) {
			usingLog.setTime(clientInfo.getTime());
		}
		if(usingLog.getMember_id() == null) {
			usingLog.setMember_id(clientInfo.getMemberId());
		}
		
		if(usingLog instanceof RuleLog) {
			RuleLog ruleLog = (RuleLog) usingLog;
			if(ruleLog.getUsing_log_no() == 0) { //아직 사용 로그를 등록하지 않은 경우
				memberDao.insertIntoUsingLog(usingLog);
				ruleLog.setUsing_log_no(getNoOfUsingLog(usingLog));
			}
			ruleDao.insertIntoRuleLog(ruleLog);
		}
		else if(usingLog instanceof UtteranceLog){
			ruleDao.insertIntoUtteranceLog((UtteranceLog)usingLog);
		}
		else {
			memberDao.insertIntoUsingLog(usingLog);
		}
	}
	
	/**
	 * UsingLog 테이블에 사용 로그를 등록하고, RuleLog의 외래키로 사용할 UsingLog의 기본키를 반환
	 * @param content UsingLog 테이블의 내용
	 * @return RuleLog의 외래키로 사용할 UsingLog의 기본키
	 */
	public int insertUsingLog(String content) {
		UsingLog usingLog = new UsingLog();
		usingLog.setContent(content);
		setUsingLog(usingLog);
		int key = getNoOfUsingLog(usingLog);
		return key;
	}
	
	/**
	 * 사용 로그의 Primary Key인 순번을 가져옴
	 * @param usingLog 사용 로그 객체
	 * @return 사용 로그의 Primary Key인 순번
	 */
	public int getNoOfUsingLog(UsingLog usingLog) {
		return memberDao.getUsingLog(usingLog).getNo();
	}
	
	/**
	 * 디테일한 룰 로그를 DB에 등록함
	 * @param ruleLogDetail 디테일한 룰 로그
	 * @return DB에 추가된 row의 수
	 */
	public int insertRuleLogDetail(RuleLog ruleLogDetail) {
		return ruleDao.insertIntoRuleLogDetail(ruleLogDetail);
	}
}
