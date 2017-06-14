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
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
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
	@Autowired
	private CartService cartService;
	@Value("${CART_EXPIRE}")
	private Integer cartExpire;
	
	//添加商品到购物车
	@RequestMapping("/cart/add/{itemId}")
	public String addItemToCart(@PathVariable long itemId,@RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//登录 使用redis保存
		//判断是否有登陆信息
		TbUser user = (TbUser) request.getAttribute("user");
		//如果不为空，调用服务将购物车信息存入redis数据库
		if (user!=null) {
			//调用服务添加购物车
			cartService.addCartList(user.getId(), itemId, num);
			//显示添加成功页面
			return "cartSuccess";
		}
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
	public String showCart(HttpServletRequest request,HttpServletResponse response){
		//cookie中的购物信息
		List<TbItem> cartList = getCartList(request);
		TbUser user = (TbUser) request.getAttribute("user");
		//有用户登陆的情况  展示购物车
		if (user!=null) {
			//合并购物车
			cartService.mergeCartList(cartList,user.getId());
			//清除cookie中保存的购物车的信息
			CookieUtils.deleteCookie(request, response, "cart");
			//将合并后的购物车信息查询出来
			cartList = cartService.getCartList(user.getId());
			//将cookie中存在的商品信息添加到cartList中
		}
		//将数据显示到前台页面
		request.setAttribute("cartList", cartList);
		return "cart";
	}
	//更改购物车中商品的数量
	//请求路径http://localhost:8189/cart/update/num/149638795947936/4.action
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateNum(@PathVariable long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		//有用户登陆的情况  修改购物车
		if (user!=null) {
			cartService.updateCart(user.getId(), itemId, num);			
			return E3Result.ok();
		}
		//根据商品id修改cookie中已存在的商品的数量
		List<TbItem> cartList = getCartList(request);
		//遍历查询要修改的商品
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().equals(itemId)) {
				//查到该商品将商品的num设置为num
				tbItem.setNum(num);
				break;
			}
		}
		//将更改的信息写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), cartExpire, true);
		return E3Result.ok();
	}
	//删除购物车中的某项
	//请求路径http://localhost:8189/cart/delete/149699750806573.html
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable long itemId,HttpServletRequest request,
			HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		//有用户登陆的情况  删除购物车某项
		if (user!=null) {
			cartService.deleteCart(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		//获取购物车
		List<TbItem> cartList = getCartList(request);
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().equals(itemId)) {
				cartList.remove(tbItem);
				break;
			}
		}
		//更新到cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), cartExpire, true);
		//跳转请求  再次展示购物车列表
		return "redirect:/cart/cart.html";
	}
}	
