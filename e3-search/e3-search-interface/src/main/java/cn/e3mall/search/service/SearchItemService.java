package cn.e3mall.search.service;

import cn.e3mall.common.pojo.E3Result;

public interface SearchItemService {
	//将数据库中的信息导入到索引库
	E3Result importItemsToSolr() throws Exception;
}
