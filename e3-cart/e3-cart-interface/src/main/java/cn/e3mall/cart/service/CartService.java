package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {
	
	//添加商品到购物车
	void addCartList(long uid,long itemId,Integer num);
	//查询购物车信息
	List<TbItem> getCartList(long uid);
	//修改购物车信息
	void updateCart(long uid,long itemId,Integer num);
	//删除购物车信息
	void deleteCart(long uid,long itemId);
	void mergeCartList(List<TbItem> cartList, Long id);
	//清空购物车信息
	E3Result clearCart(long userId);
}
