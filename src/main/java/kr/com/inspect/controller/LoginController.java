package kr.com.inspect.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.com.inspect.dto.User;
import kr.com.inspect.service.LoginService;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;

	/* 회원가입 */
	@RequestMapping(value = "/insertUser", method = RequestMethod.POST)
	public String insertUser(User user, Model model) {
		
		int result = loginService.insertUser(user);
		if (result == 0) {
			model.addAttribute("message", "같은 아이디가 있습니다.");
			return "wrong";
		}
		return "index";
	}
	
	/* 아이디 중복 체크 */
	@RequestMapping(value = "/IdChk", method = RequestMethod.POST)
	public int IdChk(User user) throws Exception {
		int result = loginService.IdChk(user);
		return result;
	}
	/* 로그인 */
	@RequestMapping(value = "/loginUser", method = RequestMethod.POST)
	public String loginUser(User user, Model model, HttpSession session) throws Exception {
		try {
			User result = loginService.loginUser(user);
			session.setAttribute("loginId", result.getUserid());
		} catch (NullPointerException e) {
			model.addAttribute("msg", "The user id or password is incorrect.                                  Please re-enter your Id and Password");
			model.addAttribute("url","/");
			return "redirect";
		}
		return "navigator";
	}

	/* Navigator로 이동 */
	@GetMapping("/goNavi")
	public String goNavi() {
		return "/navigator";
	}

	/* 로그아웃 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(User user, Model model, HttpSession session) {
		session.invalidate();
		// session.setAttribute("loginId",null); 으로 해줘도 된다.
		return "index";
	}



	/* 회원가입 페이지 이동 */
	@GetMapping("/insert")
	public String moveToElasticPage() {
		return "/insert";
	}

	/*
	 * @RequestMapping(value = "/test", method = RequestMethod.GET) public String
	 * test(User user, Model model, HttpSession session) { //로그인 값을 계속 가지고 있는
	 * Session TEST
	 * 
	 * System.out.println((String) session.getAttribute("loginId"));
	 * 
	 * return "main"; }
	 */

}