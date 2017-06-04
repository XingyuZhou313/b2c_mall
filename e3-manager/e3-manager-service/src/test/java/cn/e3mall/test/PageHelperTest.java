package cn.e3mall.test;

import java.util.List;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;

public class PageHelperTest {
	
	@Test
	public void test(){
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		TbItemMapper itemMapper = applicationContext.getBean(TbItemMapper.class);
		//设置分页信息
		PageHelper.startPage(1, 15);
		TbItemExample example=new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//pageInfo取分页信息    包括 总条数
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		//总条数
		long total = pageInfo.getTotal();
		//总页数
		int pages = pageInfo.getPages();
		//当前页数
		int pageNum = pageInfo.getPageNum();
		System.out.println("每页显示条数"+pageInfo.getPageSize());
		//当前页存在的信息
		List<TbItem> list2 = pageInfo.getList();
		System.out.println(total);
		System.out.println(pages);
		System.out.println(pageNum);
		for (TbItem tbItem : list2) {
			System.out.println(tbItem.getImage());
		}
		
	}
}
