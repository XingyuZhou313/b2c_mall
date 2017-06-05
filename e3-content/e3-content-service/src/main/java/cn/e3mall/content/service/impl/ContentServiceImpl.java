package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.json.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

/**
 * 内容 管理的 service
 * 
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private TbContentMapper tbContentMapper;
	@Autowired
	private JedisClient jedisClient;
	// 根据内容类别 分页查询内容列表
	public EasyUIResult pageQuery(long categoryId, int page, int rows) {
		PageHelper.startPage(page, rows);
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		EasyUIResult result = new EasyUIResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	// 新增内容信息
	public E3Result addContent(TbContent tbContent) {
		//完善信息  id为自增长 不需理会
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		tbContentMapper.insert(tbContent);
		//因加入了redis缓存  所以在进行增删改操作时应改变缓存中内容
		//同步缓存操作
		jedisClient.hdel("CONTENT_INFO", tbContent.getCategoryId().toString());
		return E3Result.ok();
	}
	//修改内容信息
	public E3Result updateContent(TbContent tbContent) {
		tbContent.setUpdated(new Date());
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		//同步缓存操作
		jedisClient.hdel("CONTENT_INFO", tbContent.getCategoryId().toString());
		return E3Result.ok();
	}
	//删除内容信息
	public E3Result deleteContent(long[] ids) {
		TbContent tbContent = tbContentMapper.selectByPrimaryKey(ids[0]);
		for (long id : ids) {
			tbContentMapper.deleteByPrimaryKey(id);
		}
		//同步缓存操作
		jedisClient.hdel("CONTENT_INFO", tbContent.getCategoryId().toString());
		return E3Result.ok();
	}

	/**
	 * 根据cid查询内容列表
	 */
	public List<TbContent> getTbContentByCid(long categoryId) {
		//加入redis缓存
		try {
			//先查redis数据库  如果有则直接返回查询结果 不能对原有业务逻辑产生影响 所有try起来
			String result = jedisClient.hget("CONTENT_INFO", categoryId+"");
			if (StringUtils.isNotBlank(result)) {
				return JsonUtils.jsonToList(result, TbContent.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = tbContentMapper.selectByExample(example);
		try {
			//如果redis中没有  则查询关系型数据库  并将数据存入redis  不能对原有业务逻辑产生影响 所有try起来
			jedisClient.hset("CONTENT_INFO", categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
