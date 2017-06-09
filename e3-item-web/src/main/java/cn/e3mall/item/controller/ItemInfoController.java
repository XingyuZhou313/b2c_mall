package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemCatService;
import cn.e3mall.service.ItemService;

/**
 * 商品详情 controller
 * @author Administrator
 *
 */
@Controller
public class ItemInfoController {
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemCatService ItemCatService;
	@RequestMapping("/list/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model){
		//根据id查询商品详细信息
		TbItem tbItem = itemService.getItemById(itemId);
		//根据id查询商品描述信息
		TbItemDesc tbItemDesc = itemService.getItemDescById(itemId);
		
		
		
		return "item";
	}
	
}
