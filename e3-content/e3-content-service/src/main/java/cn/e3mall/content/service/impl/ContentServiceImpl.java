package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
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
		return E3Result.ok();
	}
	//修改内容信息
	public E3Result updateContent(TbContent tbContent) {
		tbContent.setUpdated(new Date());
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		return E3Result.ok();
	}
	//删除内容信息
	public E3Result deleteContent(long[] ids) {
		for (long id : ids) {
			tbContentMapper.deleteByPrimaryKey(id);
		}
		return E3Result.ok();
	}

}
