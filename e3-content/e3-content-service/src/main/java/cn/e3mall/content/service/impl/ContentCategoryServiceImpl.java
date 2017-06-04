package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.zookeeper.Op.Create;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyTreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类管理  服务
 * @author Administrator
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	
	//根据父节点id  查询子节点列表  内容分类列表的展示
	public List<EasyTreeNode> getContentCategoryList(long parentId) {
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		List<EasyTreeNode>  result = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyTreeNode treeNode = new EasyTreeNode();
			treeNode.setId(tbContentCategory.getId());
			treeNode.setText(tbContentCategory.getName());
			treeNode.setState(tbContentCategory.getIsParent()?"closed":"open");
			result.add(treeNode);
		}
		return result;
	}

	/**
	 * 添加分类信息
	 */
	public E3Result addContentCategory(TbContentCategory tbContentCategory) {
		//将刚添加的分类信息保存  id为自增字段 
		//该类目是否为父类目，1为true，0为false
		tbContentCategory.setIsParent(false);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		//插入数据库  在xml文件中配置selectKey 返回主键
		tbContentCategoryMapper.insert(tbContentCategory);
		//根据父类节点id查询该分类的isParent是否为false
		TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
		//判断该分类信息是否为父节点
		if (!contentCategory.getIsParent()) {
			contentCategory.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(contentCategory);
		}
		//将刚增加的节点id返回页面
		return E3Result.ok(tbContentCategory);
	}

	//修改分类信息
	public E3Result updateContentCategory(TbContentCategory tbContentCategory) {
		tbContentCategory.setUpdated(new Date());
		tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
		return E3Result.ok();
	}
	//删除分类信息
	public E3Result deleteContentCategory(TbContentCategory tbContentCategory) {
		Long id = tbContentCategory.getId();
		TbContentCategory category = tbContentCategoryMapper.selectByPrimaryKey(id);
		Long  parentId= category.getParentId();
		//删除分类信息
		tbContentCategoryMapper.deleteByPrimaryKey(id);
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		//判断该分类信息父节点中是否还有其它类  如果没有  将父节点的isParent字段改为0
		if (list==null||list.size()==0) {
			TbContentCategory parentContentCategory = tbContentCategoryMapper.selectByPrimaryKey(parentId);
			parentContentCategory.setIsParent(false);
			tbContentCategoryMapper.updateByPrimaryKeySelective(parentContentCategory);
		}
		return E3Result.ok();
	}

}
