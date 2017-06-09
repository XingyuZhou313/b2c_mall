package cn.e3mall.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
//test  设置监听类
public class MyMessageListener implements MessageListener{

	@Override
	public void onMessage(Message message) {
		//转换为TextMessage对象
		TextMessage textMessage=(TextMessage) message;
		//从textMessage对象中取数据
		String text;
		try {
			text = textMessage.getText();
			System.out.println(text);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
