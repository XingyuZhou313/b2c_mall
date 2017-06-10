package cn.e3mall.test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemakerTest {
	
	
	//freemarker测试
	@Test
	public void testFreemaker() throws Exception{
		//加入jar包依赖
		//创建一个configuration对象，直接new一个对象。构造方法的参数就是freemaker对应的版本号
		Configuration configuration = new Configuration(Configuration.getVersion());
		//设置模板文件所在的路径。
		configuration.setDirectoryForTemplateLoading(new File("F:/e3mall-Git/e3-item-web/src/main/webapp/ftl"));
		//设置模板文件使用的字符集，一般就是utf-8
		configuration.setDefaultEncoding("utf-8");
		//加载一个模板，创建一个模板对象
		Template template = configuration.getTemplate("hello.ftl");
		//创建一个模板使用的数据集，可以是pojo也可以是map，一般是map  比较灵活
		Map data = new HashMap<>();
		data.put("hello", "hello freemarker");
		//创建一个Writer对象，一般创建一个FileWriter对象，指定生成的文件名
		Writer writer = new FileWriter(new File("F:/e3mall-Git/e3-item-web/src/main/webapp/ftl/h.txt"));
		//调用模板对象的process方法输出文件
		template.process(data, writer);
		//关闭流
		writer.close();
		
	}
	
	@Test
	public void FreemarkerGen() throws Exception{
		Configuration configuration = new Configuration(Configuration.getVersion());
		configuration.setDirectoryForTemplateLoading(new File("F:/e3mall-Git/e3-item-web/src/main/webapp/ftl"));
		configuration.setDefaultEncoding("utf-8");
		Template template = configuration.getTemplate("student.ftl");
		Map data=new HashMap<>();
//		data.put("stu", new Student(0, "zxy"));
		List<Student> alist = new ArrayList<>();
		alist.add(new Student(0,"zxy"));
		alist.add(new Student(1, "gjj"));
		alist.add(new Student(3, "zxj"));
		data.put("students", alist);
		Writer out=new FileWriter(new File("F:/e3mall-Git/e3-item-web/src/main/webapp/ftl/student.html"));
		template.process(data, out);
		out.close();
	}
}
