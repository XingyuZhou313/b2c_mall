package cn.e3mall.sso.controller;

import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 注册controller
 * @author Administrator
 *
 */
@Controller
public class RegisterController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/page/register")
	public String showRegitster() {
		return "register";
	}
	@RequestMapping("/user/check/{data}/{type}")
	@ResponseBody
	public E3Result checkUser(@PathVariable String data,@PathVariable Integer type){
		//动态查询数据库中是否有存在的内容   
		E3Result e3Result = userService.checkUser(data, type);
		return e3Result;
	}
	//用户的注册   限定只接收get请求
	@RequestMapping(value="/user/register",method=RequestMethod.GET)
	@ResponseBody
	public E3Result register(TbUser user){
		E3Result result = userService.addUser(user);
		return result;
	}
}
