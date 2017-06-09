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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.common.utils.IDUtils;
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
	
	@Override
	public TbItem getItemById(long itemId) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
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
		return itemDescMapper.selectByPrimaryKey(itemDescId);
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
