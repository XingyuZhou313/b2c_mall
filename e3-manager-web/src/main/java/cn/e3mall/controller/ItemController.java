package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemParam;
import cn.e3mall.service.ItemService;

/**
 * 商品管理contrller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	/**
	 * 根据id查询商品
	 * @param itemId
	 * @return
	 */
	@RequestMapping("/item/{id}")
	@ResponseBody
	public TbItem getItemById(@PathVariable("id") Long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	//分页查询商品
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIResult pageQuery(Integer page,Integer rows){
		EasyUIResult easyUIResult = itemService.pageQuery(page, rows);
		return easyUIResult;
	}
	//添加商品
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem tbItem,String desc){
		E3Result result = itemService.addItem(tbItem, desc);
		return result;
	}
	//批量删除商品
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public E3Result deleteItem(long[] ids){
		E3Result result = itemService.deleteItem(ids);
		return result;
	}
	@RequestMapping("/rest/page/item-edit")
	public String showItemEdit(){
		return "item-edit";
	}
	//回显描述信息
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result showItemDescById(@PathVariable("id")long itemDescId){
		TbItemDesc data=itemService.getItemDescById(itemDescId);
		E3Result e3Result = new E3Result();
		e3Result.setData(data);
		e3Result.setStatus(200);
		return e3Result;
	};
	//回显商品规格信息
	@RequestMapping("/rest/item/param/item/query/{id}")
	@ResponseBody
	public E3Result showItemParamById(@PathVariable("id")long itemId){
		TbItemParam data=itemService.getItemParamById(itemId);
		E3Result e3Result = new E3Result();
		e3Result.setData(data);
		e3Result.setStatus(200);
		return e3Result;
	}
	@RequestMapping(value="/rest/item/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateItem(TbItem tbItem,String desc){
		E3Result result = itemService.updateItem(tbItem, desc);
		return result;
	}
	
	//商品下架
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public E3Result instock(long[] ids){
		E3Result result = itemService.instock(ids);
		return result;
	}
	//商品下架
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public E3Result reshelf(long[] ids){
		E3Result result = itemService.reshelf(ids);
		return result;
	}
	
}
