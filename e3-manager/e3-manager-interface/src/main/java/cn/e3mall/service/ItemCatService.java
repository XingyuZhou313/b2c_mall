package cn.e3mall.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyTreeNode;

public interface ItemCatService {
	
	List<EasyTreeNode> showItemCatList(long parentId);
	
}
