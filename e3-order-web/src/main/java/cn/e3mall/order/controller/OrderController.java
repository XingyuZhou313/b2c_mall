package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbOrder;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbUser;

/**
 * 订单管理controller
 * 
 * @author Administrator
 *
 */
@Controller
public class OrderController {
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;

	// 展示订单所包含的商品信息
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		// 取得登陆信息
		TbUser user = (TbUser) request.getAttribute("user");
		// 取该用户购物车列表信息
		List<TbItem> cartList = cartService.getCartList(user.getId());
		// 将商品信息传递给jsp页面展示
		request.setAttribute("cartList", cartList);
		// 返回逻辑视图
		return "order-cart";
	}

	// 创建订单
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {

		// 取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		// 设置用户信息
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		// 生成订单
		E3Result e3Result = orderService.createOrder(orderInfo);
		request.setAttribute("orderId", e3Result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		// 清空购物车数据
		cartService.clearCart(user.getId());
		return "success";

	}
}
