package cn.e3mall.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 前太入口 controller
 * @author Administrator
 *
 */
@Controller
public class IndexController {
	@Value("${index.slider.cid}")
	private long indexSliderCid;
	
	@Autowired
	private ContentService contentService;
	
	//显示首页信息 根据cid查询出内容信息
	@RequestMapping("/index")
	public String showIndex(Model model){
		List<TbContent> ad1List = contentService.getTbContentByCid(indexSliderCid);
		model.addAttribute("ad1List", ad1List);
		return "index";
	}
}
