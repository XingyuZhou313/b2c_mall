package cn.e3mall.search_test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class solrTest2 {

	
	//使用solr实现查询
	@Test
	public void test() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-solr.xml");
		SolrServer solrServer = applicationContext.getBean(HttpSolrServer.class);
		//创建solrQuery查询对象
		SolrQuery query = new SolrQuery();
		//设置条件
		query.setQuery("星");
		//设置默认查询域
		query.set("df", "item_keywords");
		//设置分页信息
		query.setStart(0);
		query.setRows(5);
		//设置高亮显示
		//开启高亮
		query.setHighlight(true);
		//设置高亮域
		query.addHighlightField("item_title");
		//设置前后缀
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//使用solrServer对象进行查询,获得response对象
		QueryResponse response = solrServer.query(query);
		//取得高亮显示结果
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//获取results对象
		SolrDocumentList results = response.getResults();
		for (SolrDocument solrDocument : results) {
			String itemTitle=null;
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if (list!=null&&list.size()>0) {
				itemTitle=list.get(0);
			}else{
				itemTitle=(String) solrDocument.get("item_title");
			}
			System.out.println(solrDocument.get("id"));
			System.out.println(itemTitle);
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
		}
	}
	@Test
	public void test1() throws IOException{
		System.in.read();
		System.out.println(Calendar.ALL_STYLES);
	}
}
