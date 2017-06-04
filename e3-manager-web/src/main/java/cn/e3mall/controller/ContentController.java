package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIResult;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 内容管理controller
 * @author Administrator
 *
 */
@Controller
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	//分页显示某类别下的内容信息
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIResult pageQuery(long categoryId,int page,int rows){
		EasyUIResult result = contentService.pageQuery(categoryId, page, rows);
		return result;
	}
	//添加内容信息   类别在前台已指定
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result addContent(TbContent tbContent){
		E3Result result = contentService.addContent(tbContent);
		return result;
	}
	//修改内容信息  
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result editContent(TbContent tbContent){
		E3Result result = contentService.updateContent(tbContent);
		return result;
	}
	//删除内容信息
	@RequestMapping("/content/delete")
	@ResponseBody
	public E3Result deleteContentById(long[] ids){
		E3Result result = contentService.deleteContent(ids);
		return result;
	}
}
