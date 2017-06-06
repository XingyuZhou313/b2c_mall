package cn.e3mall.search_test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.management.Query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

public class SolrJTest {
	
	//向索引库添加
	@Test
	public void testSolrj() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", 1);
		doc.addField("item_title", "周星宇");
		doc.addField("item_price", 100);
		solrServer.add(doc);
		solrServer.commit();
	}
	//从索引库删除内容
	@Test
	public void deleteFromSolrj() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
//		solrServer.deleteById("1");
		solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}
	//查询
	@Test
	public void searchFromSolr() throws Exception{
		//创建solrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
		//创建solrQuery对象
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		//获得response对象
		QueryResponse response = solrServer.query(query);
		//获取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		//总条数
		long numFound = solrDocumentList.getNumFound();
		System.out.println("总条数为:"+numFound);
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_title"));
		}
		
	}
	//查询带高亮显示
	@Test
	public void searchFromSolrWithHighLight() throws Exception{
		//创建solrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
		//创建solrQuery对象
		SolrQuery query = new SolrQuery();
		query.setQuery("周");
		query.set("df", "item_keywords");
		//开启高亮
		query.setHighlight(true);
		//高亮显示字段
		query.addHighlightField("item_title");
		//高亮前缀
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//高亮后缀
		//获得response对象
		QueryResponse response = solrServer.query(query);
		//获取查询结果
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		SolrDocumentList solrDocumentList = response.getResults();
		//总条数
		long numFound = solrDocumentList.getNumFound();
		System.out.println("总条数为:"+numFound);
		for (SolrDocument solrDocument : solrDocumentList) {
			String item_title=null;
			System.out.println(solrDocument.get("id"));
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if (list!=null&&list.size()>0) {
				item_title=list.get(0);
			}else{
				item_title=(String) solrDocument.get("item_title");
			}
			System.out.println(item_title);
			System.out.println(solrDocument.get("item_price"));
		}
		
	}
}
