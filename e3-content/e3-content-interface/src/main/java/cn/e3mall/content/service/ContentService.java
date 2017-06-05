package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.pojo.TbContent;

public interface ContentService {
	//内容的分页展示
	EasyUIResult pageQuery(long categoryId,int page,int rows);
	//新增内容信息
	E3Result addContent(TbContent tbContent);
	//修改内容信息
	E3Result updateContent(TbContent tbContent);
	//删除内容信息
	E3Result deleteContent(long[] ids);
	//根据内容类别展示内容信息
	List<TbContent> getTbContentByCid(long categoryId);
}
