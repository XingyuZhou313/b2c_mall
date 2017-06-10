package cn.e3mall.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 生成静态html文件controller
 * @author Administrator
 *
 */
@Controller
public class HtmlGenController {
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@RequestMapping("/genhtml")
	@ResponseBody
	public String genHtml() throws Exception{
		//从spring容器中获得FreeMarkerConfigurer对象
		//从freemarkerconfigurer对象中获得configuration对象
		Configuration configuration = freeMarkerConfig.getConfiguration();
		//获取模板对象
		Template template = configuration.getTemplate("hello.ftl");
		Map dataModel=new HashMap<>();
		dataModel.put("name", "周星宇");
		Writer out=new FileWriter(new File("F:/Temp/GenHtml/freemarker/hello.txt"));
		//调用模板的方法  生成静态页面
		template.process(dataModel, out);
		//关闭资源
		out.close();
		return "ok";
	}
}
