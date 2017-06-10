package cn.e3mall.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;
import cn.e3mall.search.service.SearchItemService;
/**
 * 商品搜索服务层
 * @author Administrator
 *
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SolrServer solrServer;
	@Autowired
	private ItemMapper itemMapper;
	
	//将查询到的数据导入solr索引库
	public E3Result importItemsToSolr() throws Exception {
		try {
			List<SearchItem> list = itemMapper.getItemList();
			//创建document对象 
			for (SearchItem searchItem : list) {
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", searchItem.getId());
				doc.addField("item_title", searchItem.getTitle());
				doc.addField("item_sell_point", searchItem.getSell_point());
				doc.addField("item_price", searchItem.getPrice());
				doc.addField("item_image", searchItem.getImage());
				doc.addField("item_category_name", searchItem.getCategory_name());
				//添加到索引库
				solrServer.add(doc);
			}
			//提交
			solrServer.commit();
			//返回成功
			return E3Result.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return E3Result.build(500, "商品导入失败");
		}
	}

}
