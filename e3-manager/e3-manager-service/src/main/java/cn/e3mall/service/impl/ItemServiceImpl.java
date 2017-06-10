package cn.e3mall.service.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemParam;
import cn.e3mall.pojo.TbItemParamExample;
import cn.e3mall.pojo.TbItemParamExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemParamMapper itemParamMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;
	
	/**
	 * 根据id查询item  加入缓存
	 * 先从redis数据库中进行查询
	 * 如果redis数据库中没有 查询mysql数据库
	 * 将查出的数据存入redis 设置两小时后过期  为了节省redis的内存资源
	 */
	public TbItem getItemById(long itemId) {
		try {
			String json = jedisClient.get("ITEM_INFO:"+itemId+":BASE");
			if (StringUtils.isNotBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				System.out.println("使用了redis缓存！！");
				return tbItem;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		try {
			jedisClient.set("ITEM_INFO:"+itemId+":BASE", JsonUtils.objectToJson(tbItem));
			jedisClient.expire("ITEM_INFO:"+itemId+":BASE", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}

	/**
	 * page 当前页
	 * rows 每页显示条数
	 */
	public EasyUIResult pageQuery(int page, int rows) {
		EasyUIResult result=new EasyUIResult();
		PageHelper.startPage(page, rows);
		List<TbItem> list = itemMapper.selectByExample(new TbItemExample());
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		result.setRows(pageInfo.getList());
		return result;
	}

	/**
	 * 添加商品信息  
	 * 添加商品信息成功后 通过mq实现同步到索引库
	 */
	public E3Result addItem(TbItem tbItem, String desc) {
		final long itemId=IDUtils.genItemId();
		tbItem.setId(itemId);
		tbItem.setStatus((byte) 1);
		tbItem.setCreated(new Date());
		tbItem.setUpdated(new Date());
		itemMapper.insert(tbItem);
		TbItemDesc tbItemDesc = new TbItemDesc();
		tbItemDesc.setItemId(tbItem.getId());
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());
		tbItemDesc.setUpdated(new Date());
		itemDescMapper.insert(tbItemDesc);
		//发布消息
		//使用模板对象
		jmsTemplate.send(topicDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage();
				//发送消息  将商品id传递过去
				textMessage.setText(itemId+"");
				return textMessage;
			}
		});
		return E3Result.ok();
	}

	/**
	 * 根据商品id 批量删除商品
	 */
	public E3Result deleteItem(long[] ids) {
		for (long id: ids) {
			itemMapper.deleteByPrimaryKey(id);
			itemDescMapper.deleteByPrimaryKey(id);
		}
		return new E3Result().ok();
	}
	//商品下架
	public E3Result instock(long[] ids) {
		for (long id : ids) {
			TbItem tbItem = itemMapper.selectByPrimaryKey(id);
			tbItem.setStatus((byte) 2);
			itemMapper.updateByPrimaryKey(tbItem);
		}
		return new E3Result().ok();
	}

	//商品上架
	public E3Result reshelf(long[] ids) {
		for (long id : ids) {
			TbItem tbItem = itemMapper.selectByPrimaryKey(id);
			tbItem.setStatus((byte)1);
			itemMapper.updateByPrimaryKey(tbItem);
		}
		return new E3Result().ok();
	}

	/**
	 * 根据id查询商品描述信息
	 */
	public TbItemDesc getItemDescById(long itemDescId) {
		try {
			String json = jedisClient.get("ITEM_INFO:"+itemDescId+":DESC");
			if (StringUtils.isNotBlank(json)) {
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				System.out.println("使用了redis缓存！！");
				return itemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc =itemDescMapper.selectByPrimaryKey(itemDescId);
		try {
			jedisClient.set("ITEM_INFO:"+itemDescId+":DESC", JsonUtils.objectToJson(itemDesc));
			jedisClient.expire("ITEM_INFO:"+itemDescId+":DESC", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

	@Override
	public TbItemParam getItemParamById(long itemId) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		TbItemParamExample example=new TbItemParamExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemCatIdEqualTo(tbItem.getCid());
		List<TbItemParam> list = itemParamMapper.selectByExample(example);
		return list.get(0);
	}

	@Override
	public E3Result updateItem(TbItem tbItem, String desc) {
		tbItem.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(tbItem);
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(tbItem.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		return E3Result.ok();
	}
	
}
