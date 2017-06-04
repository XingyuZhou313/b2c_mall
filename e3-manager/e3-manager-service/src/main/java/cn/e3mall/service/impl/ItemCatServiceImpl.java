package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyTreeNode;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemCatExample.Criteria;
import cn.e3mall.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	/**
	 * 根据parentId查询  分类树节点
	 */
	public List<EasyTreeNode> showItemCatList(long parentId) {
		List<EasyTreeNode> nodes=new ArrayList<>();
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		//设置条件  指定父节点
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		for (TbItemCat tbItemCat : list) {
			EasyTreeNode node = new EasyTreeNode();
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			nodes.add(node);
		}
		return nodes;
	}
	
}
