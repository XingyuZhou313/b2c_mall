package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3mall.common.pojo.SearchItem;

public interface ItemMapper {
	//查询所有的索引库需要的数据
	List<SearchItem> getItemList();
}
