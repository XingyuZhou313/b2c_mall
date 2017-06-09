package cn.e3mall.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

//商品添加监听
public class ItemAddListener implements MessageListener {
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SolrServer solrServer;
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=(TextMessage) message;
		//获取添加的商品id
		try {
			Long itemId = Long.parseLong(textMessage.getText());
			//根据商品id查询到SearchItem对象
			SearchItem searchItem = itemMapper.getSearchItemById(itemId);
			//创建documen对象
			SolrInputDocument document = new SolrInputDocument();
			Thread.sleep(1000);
			// 向文档对象中添加域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			//将商品加入索引库
			solrServer.add(document);
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
