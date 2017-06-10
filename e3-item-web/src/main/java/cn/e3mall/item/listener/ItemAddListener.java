package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemAddListener implements MessageListener {
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Value("${html.gen.path}")
	private String htmlGenPath;
	@Override
	public void onMessage(Message message) {
		
		try {
			//获取订阅消息
			TextMessage textMessage=(TextMessage) message;
			Long itemId=new Long(textMessage.getText());
			//查询tbItem对象
			TbItem tbItem = itemService.getItemById(itemId);
			//查询itemDesc对象
			TbItemDesc itemDesc = itemService.getItemDescById(itemId);
			Item item = new Item(tbItem);
			//获取configuration对象
			Configuration configuration = freeMarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			Map dataModel=new HashMap<>(); 
			dataModel.put("item", item);
			dataModel.put("itemDesc", itemDesc);
			Writer out=new FileWriter(new File(htmlGenPath+itemId+".html"));
			Thread.sleep(2000);
			template.process(dataModel, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
