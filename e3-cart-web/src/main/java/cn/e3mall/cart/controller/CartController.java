package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

/**
 * 购物车管理controller
 * @author Administrator
 *
 */
@Controller
public class CartController {
	
	@Autowired
	private ItemService itemService;
	@Value("${CART_EXPIRE}")
	private Integer cartExpire;
	
	//添加商品到购物车
	@RequestMapping("/cart/add/{itemId}")
	public String addItemToCart(@PathVariable long itemId,@RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request,HttpServletResponse response){
		String cookieValue;
		//登录 使用redis保存
		//不登陆 使用cookie
		//设置变量  表示购物车中是否存在该商品   false代表不存在  true 代表存在
		boolean flag=false;
		//取购物车信息
		List<TbItem> cartList = getCartList(request);
		//判断购物车中是否存在要添加的商品 
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().equals(itemId)) {
				//如果有相同的商品   数量做加操作 break
				tbItem.setNum(tbItem.getNum()+num);
				//将旗帜变量设置为true 代表有相同商品存在
				flag=true;
				break;
			}
		}
		//如果没有查询到有相同产品的存在
		if (!flag) {
			//调用服务查询商品信息  设置商品数量  处理商品图片
			TbItem item = itemService.getItemById(itemId);
			item.setNum(num);
			//处理图片  设置为一张
			if (item.getImage()!=null) {
				String[] images = item.getImage().split(",");
				item.setImage(images[0]);
			}
			//将商品信息加入购物车
			cartList.add(item);
		}
		//将购物车信息存入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), cartExpire, true);
		//添加购物车成功，显示成功页面
		return "cartSuccess";
	}
	//购物车信息  保存添加的商品
	private List<TbItem> getCartList(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "cart", true);
		//判断cookie中是否有商品
		if (StringUtils.isBlank(json)) {
			//如果没有商品  返回一个空购物车用来存放商品
			return new ArrayList<>();
		}
		return JsonUtils.jsonToList(json, TbItem.class);
	}
	//显示购物车列表
	@RequestMapping("/cart/cart")
	public String showCart(HttpServletRequest request){
		List<TbItem> cartList = getCartList(request);
		//将查询到的数据显示到前台页面
		request.setAttribute("cartList", cartList);
		return "cart";
	}
}	
