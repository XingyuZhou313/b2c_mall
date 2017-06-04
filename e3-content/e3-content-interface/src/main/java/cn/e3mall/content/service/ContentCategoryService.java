package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyTreeNode;
import cn.e3mall.pojo.TbContentCategory;

public interface ContentCategoryService {
	//获取内容分类树形列表
	List<EasyTreeNode> getContentCategoryList(long parentId);
	//添加分类  
	E3Result addContentCategory(TbContentCategory tbContentCategory);
	//修改分类  
	E3Result updateContentCategory(TbContentCategory tbContentCategory);
	//删除分类
	E3Result deleteContentCategory(TbContentCategory tbContentCategory);
}
