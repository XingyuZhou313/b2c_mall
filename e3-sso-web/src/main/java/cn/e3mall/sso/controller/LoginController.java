package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 用户登陆controller
 * @author Administrator
 *
 */
@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	//显示登陆界面
	@RequestMapping("/page/login")
	public String showLogin(String redirect,Model model){
		model.addAttribute("redirect", redirect);
		return "login";
	}
	
	//用户登陆
	@RequestMapping("/user/login")
	@ResponseBody
	public E3Result login(TbUser user,HttpServletRequest request,HttpServletResponse response){
		E3Result result = userService.login(user);
		//取出token信息
		//判断token信息是否为空 为空直接返回result 说明么有登陆成功
		if (result.getData()==null) {
			return result;
		}
		String token = result.getData().toString();
		//不为空  将token信息写入浏览器的cookie中进行保存
		CookieUtils.setCookie(request, response, "COOKE_TOKEN_KEY", token);
		return result;
	}
/*	@RequestMapping(value="/user/token/{token}",produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getLoginUserByToken(@PathVariable String token,String callback){
		E3Result result = userService.getLoginUserByToken(token);
		if (StringUtils.isNotBlank(callback)) {
			return callback+"("+JsonUtils.objectToJson(result)+");";
		}
		return JsonUtils.objectToJson(result);
	}*/
	
	//jsonp方案的实现
	@RequestMapping(value="/user/token/{token}",produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object getLoginUserByToken(@PathVariable String token,String callback){
		E3Result result = userService.getLoginUserByToken(token);
		if (StringUtils.isNotBlank(callback)) {
			MappingJacksonValue jacksonValue = new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}
		return result;
	}
	//用户的退出
	@RequestMapping("/user/logout/{token}")
	public String logout(@PathVariable String token){
		userService.logout(token);
		return "login";
	}
}
