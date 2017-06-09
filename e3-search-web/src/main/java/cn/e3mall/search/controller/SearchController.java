package cn.e3mall.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

/**
 * 商品查询controller
 * @author Administrator
 *
 */
@Controller
public class SearchController {
	
	@Autowired
	private SearchService searchService;
	@Value("${PAGE_ROWS}")
	private Integer ROWS;
	//根据关键词进行查询
	@RequestMapping("/search")
	public String search(
			String keyword,@RequestParam(defaultValue="1") Integer page,Model model) throws Exception{
		//解决乱码 
		keyword = new String(keyword.getBytes("iso8859-1"), "utf-8");
		SearchResult searchResult = searchService.search(keyword, page, ROWS);
		List<SearchItem> itemList = searchResult.getItemList();
		//回显数据
		model.addAttribute("query", keyword);
		model.addAttribute("page", page);
		//填充数据
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("recourdCount", searchResult.getRecourdCount());
		model.addAttribute("itemList", itemList);
		
		return "search";
	}
	
}
