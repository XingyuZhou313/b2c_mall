package cn.e3mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	/**
	 * 初始化访问 显示主页
	 * @return
	 */
	@RequestMapping("/")
	public String init(){
		return "index";
	}
	/**
	 * 配置  页面跳转 根据请求页面的不同  进行页面的跳转
	 */
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
}
