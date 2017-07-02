package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

/**
 * 订单服务
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbOrderMapper orderMapper; 
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	//订单id
	@Value("${order.id.genkey}")
	private String orderIdGenKey;
	//订单id开始值
	@Value("${order.id.start}")
	private String orderIdStart;
	//订单项id
	@Value("${order.item.id.genkey}")
	private String orderItemIdGenKey;
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		//接收表单的数据
		//生成订单id  使用redis的incr指令  从100544开始
		if (!jedisClient.exists(orderIdGenKey)) {
			//不存在  生成一个id
			jedisClient.set(orderIdGenKey, orderIdStart);
		}
		//存在  取i一个值
		//完善pojo的属性
		//订单id
		String orderId = jedisClient.incr(orderIdGenKey).toString();
		orderInfo.setOrderId(orderId);
		//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//向订单表插入数据
		orderMapper.insert(orderInfo);
		//获取订单项  向订单项表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			//给订单项设置id
			tbOrderItem.setId(jedisClient.incr(orderItemIdGenKey).toString());
			//将订单id 插入  完善表关系
			tbOrderItem.setOrderId(orderId);
			//插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		//获取物流信息
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		//完善物流信息属性
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		//插入到物流信息表
		orderShippingMapper.insert(orderShipping);
		//返回成功 信息  将订单id封装返回
		return E3Result.ok(orderId);
	}

	
	public static void main(String[] args) {
		int i;
		int result=0;
		switch (i) {
		case 1:
			result=result+i;
			break;
		case 2:
			result=result+i*2;
			break;
		case 3:
			result=result+i*3;
			break;
		}
		return result; 
	}
}
