package cn.e3mall.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemParam;

public interface ItemService {
	//根据id查询商品信息
	TbItem getItemById(long itemId);
	//后台分页
	EasyUIResult pageQuery(int page,int rows);
	//添加商品
	E3Result addItem(TbItem tbItem,String desc);
	//根据商品的ids删除商品
	E3Result deleteItem(long[] ids);
	//商品下架2
	E3Result instock(long[] ids);
	//商品上架1
	E3Result reshelf(long[] ids);
	//根据id查询商品描述信息
	TbItemDesc getItemDescById(long itemDescId);
	//根据id查询商品规格信息
	TbItemParam getItemParamById(long itemId);
	//修改商品
	E3Result updateItem(TbItem tbItem,String desc);
}
