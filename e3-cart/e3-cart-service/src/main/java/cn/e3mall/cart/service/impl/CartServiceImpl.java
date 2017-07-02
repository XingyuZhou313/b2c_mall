package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

/**
 * 购物车服务
 * @author Administrator
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;
	//添加商品到购物车
	public void addCartList(long uid, long itemId, Integer num) {
		//使用redis数据库中 hash数据结构 每个元素都对应一个用户和用户对应的购物车
		//key：cart:uid field:itemId value:item
		//先查询redis中是否存在该商品
		Boolean hexists = jedisClient.hexists("cart:"+uid, itemId+"");
		//如果redis中不存在该商品信息  根据id查询item
		if (!hexists) {
			TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
			tbItem.setNum(num);
			if (tbItem.getImage()!=null) {
				tbItem.setImage(tbItem.getImage().split(",")[0]);
			}
			//将商品信息存入该用户对应的购物车
			jedisClient.hset("cart:"+uid, itemId+"", JsonUtils.objectToJson(tbItem));
			return;
		}
		//如果redis中存在该商品信息  取出对应信息
		String json = jedisClient.hget("cart:"+uid, itemId+"");
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		//将数量更新
		tbItem.setNum(tbItem.getNum()+num);
		//重新存入redis
		jedisClient.hset("cart:"+uid, itemId+"", JsonUtils.objectToJson(tbItem));
	}
	//查询某用户的购物车列表
	public List<TbItem> getCartList(long uid) {
		List<String> list = jedisClient.hvals("cart:"+uid);
		List<TbItem> cartList = new ArrayList<>();
		for (String json : list) {
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			cartList.add(tbItem);
		}
		return cartList;
	}
	//修改redis中该用户的商品信息
	public void updateCart(long uid, long itemId, Integer num) {
		//取出该商品信息
		String json = jedisClient.hget("cart:"+uid, itemId+"");
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		tbItem.setNum(num);
		jedisClient.hset("cart:"+uid, itemId+"", JsonUtils.objectToJson(tbItem));
	}
	@Override
	public void deleteCart(long uid, long itemId) {
		//定位到要删除的商品信息删除
		jedisClient.hdel("cart:"+uid,itemId+"");
	}
	//合并购物车
	public void mergeCartList(List<TbItem> cartList, Long uid) {
		for (TbItem tbItem : cartList) {
			//调用现成的方式实现 购物车的合并
			addCartList(uid, tbItem.getId(), tbItem.getNum());
		}
	}
	
	//清空购物车信息
	public E3Result clearCart(long userId) {
		jedisClient.del("cart:"+userId);
		return E3Result.ok();
	}
	
}
