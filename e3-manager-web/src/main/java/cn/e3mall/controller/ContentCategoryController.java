package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyTreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.pojo.TbContentCategory;

/**
 * 内容分类controller
 * @author Administrator
 *
 */
@Controller
public class ContentCategoryController {
	@Autowired
	private ContentCategoryService contentCategoryService;
	
	//内容类别的展示
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyTreeNode> getContentCategoryList(
			@RequestParam(name="id",defaultValue="0")long parentId){
		List<EasyTreeNode> result = contentCategoryService.getContentCategoryList(parentId);
		return result;
	}
	
	//内容类别的添加
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(TbContentCategory tbContentCategory){
		E3Result result = contentCategoryService.addContentCategory(tbContentCategory);
		return result;
	}
	//内容类别的修改
	@RequestMapping("/content/category/update")
	@ResponseBody
	public E3Result updateContentCategory(TbContentCategory tbContentCategory){
		E3Result result = contentCategoryService.updateContentCategory(tbContentCategory);
		return result;
	}
	//内容类别的删除	
	@RequestMapping("/content/category/delete")
	@ResponseBody
	public E3Result deleteContentCategory(TbContentCategory tbContentCategory){
		E3Result result = contentCategoryService.deleteContentCategory(tbContentCategory);
		return result;
	}
}
