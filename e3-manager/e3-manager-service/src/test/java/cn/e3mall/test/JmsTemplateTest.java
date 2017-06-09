package cn.e3mall.test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsTemplateTest {
	//测试  生产方 发送数据
	@Test
	public void testSpringMq(){
		/*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		//从spring容器获取模板对象
		JmsTemplate template = applicationContext.getBean(JmsTemplate.class);
		//从spring容器获取destination对象
		Queue queue = applicationContext.getBean(Queue.class);
		//使用模板对象发送信息参数一是destination对象  参数二创建messager  获取session创建TextMessage对象 
 		template.send(queue, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage();
				textMessage.setText("spring  activemq  queue");
				return textMessage;
			}
		});*/
	}
	
}
