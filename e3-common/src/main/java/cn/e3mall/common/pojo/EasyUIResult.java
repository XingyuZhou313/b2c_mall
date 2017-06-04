package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

public class EasyUIResult implements Serializable{
	//提供给easyui所需的  属性及数据
	private long total;
	private List rows;
	public long getTotal() {
		return total;
	}
	public void setTotal(long l) {
		this.total = l;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
}
