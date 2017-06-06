package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.search.service.SearchItemService;

/**
 * 商品搜索controller
 * @author Administrator
 *
 */
@Controller
public class SearchItemController {
	
	@Autowired
	private SearchItemService searchItemService;
	
	//商品一键导入索引库
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3Result importItemToIndex() throws Exception{
		return searchItemService.importItemsToSolr();
	}
}
