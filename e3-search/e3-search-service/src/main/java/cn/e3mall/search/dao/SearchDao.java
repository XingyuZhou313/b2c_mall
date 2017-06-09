package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;
	
	public SearchResult search(SolrQuery query) throws Exception {
		SearchResult searchResult=new SearchResult();
		QueryResponse response = solrServer.query(query);
		//获取高亮
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//获取结果
		SolrDocumentList results = response.getResults();
		//取得总记录树
		long recourdCount = results.getNumFound();
		ArrayList<SearchItem> itemList = new ArrayList<>();
		for (SolrDocument doc : results) {
			SearchItem searchItem = new SearchItem();
			String itemTitle=null;
			List<String> list = highlighting.get(doc.get("id")).get("item_title");
			if (list!=null&&list.size()>0) {
				itemTitle=list.get(0);
			}else{
				itemTitle=(String) doc.get("item_title");
			}
			searchItem.setId((String) doc.get("id"));
			searchItem.setTitle(itemTitle);
			searchItem.setSell_point((String) doc.get("item_sell_point"));
			searchItem.setImage((String) doc.get("item_image"));
			searchItem.setPrice((long) doc.get("item_price"));
			searchItem.setCategory_name((String) doc.get("item_category_name"));
			itemList.add(searchItem);
		}
		searchResult.setRecourdCount((int) recourdCount);
		searchResult.setItemList(itemList);
		return searchResult;
	}
	
}
