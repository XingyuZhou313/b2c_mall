package cn.e3mall.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前太入口 controller
 * @author Administrator
 *
 */
@Controller
public class PortalController {
	
	@RequestMapping("/index")
	public String showIndex(){
		return "index";
	}
}
