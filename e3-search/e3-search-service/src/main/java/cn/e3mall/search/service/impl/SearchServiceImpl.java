package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;

//商品搜索服务
@Service
public class SearchServiceImpl implements SearchService {

	@Value("${DEFAULT_FIELD}")
	private String DEFAULT_FIELD;
	@Autowired
	private SearchDao searchDao;
	
	// 根据前台的条件生成SolrQuery对象
	public SearchResult search(String keyWord, Integer page, Integer rows) throws Exception {
		// 创建solrQuery对象
		SolrQuery solrQuery = new SolrQuery();
		// 设置条件
		solrQuery.setQuery(keyWord);
		// 设置默认查询域
		// 设置分页信息
		solrQuery.setStart((page-1)*rows);
		solrQuery.setRows(rows);
		// 设置高亮显示
		solrQuery.set("df", DEFAULT_FIELD);
		// 开启高亮
		solrQuery.setHighlight(true);
		// 设置高亮域
		solrQuery.addHighlightField("item_title");
		String pre="<em style='color:red'>";
		// 设置前后缀
		solrQuery.setHighlightSimplePre(pre);
		solrQuery.setHighlightSimplePost("</em>");
		SearchResult result = searchDao.search(solrQuery);
		int totalPages=result.getRecourdCount()/rows;
		if (totalPages%rows>0) totalPages++;
		result.setTotalPages(totalPages);
		return result;
	}

}
