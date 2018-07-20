package com.online.college.portal.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.online.college.common.util.EncryptUtil;
import com.online.college.common.web.JsonView;
import com.online.college.common.web.SessionContext;
import com.online.college.core.auth.domain.AuthUser;
import com.online.college.core.auth.service.IAuthUserService;


/**
 * 用户登录 & 注册
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private IAuthUserService authUserService;
	
	/**
	 * 注册页面
	 */
	@RequestMapping(value = "/register")
	public  ModelAndView register(){
		if(SessionContext.isLogin()){
			return new ModelAndView("redirect:/index.html");
		}
		return new ModelAndView("auth/register");
	}
	
	/**
	 * 实现注册
	 */
	@RequestMapping(value = "/doRegister")
	@ResponseBody
	public String doRegister(AuthUser authUser, String identiryCode, HttpServletRequest request) {
		//验证码判断
		if(identiryCode!=null && !identiryCode.equalsIgnoreCase(SessionContext.getIdentifyCode(request))){
			return JsonView.render(2);
		}
		
		AuthUser tmpUser = authUserService.getByUsername(authUser.getUsername());
		if(tmpUser != null){
			return JsonView.render(1);
		}else{
			authUser.setPassword(EncryptUtil.encodedByMD5(authUser.getPassword()));
			authUserService.createSelectivity(authUser);
			return JsonView.render(0);
		}
	}
	
	/**
	 * 登录页面
	 */
	@RequestMapping(value = "/login")
	public  ModelAndView login(){
		if(SessionContext.isLogin()){
			return new ModelAndView("redirect:/index.html");
		}
		return new ModelAndView("auth/login");
	}
	
	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpServletRequest request) {
		SessionContext.shiroLogout();
		return new ModelAndView("redirect:/index.html");
	}
	
}
