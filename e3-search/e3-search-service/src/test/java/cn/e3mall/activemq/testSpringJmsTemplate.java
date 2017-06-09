package cn.e3mall.activemq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class testSpringJmsTemplate {
	
	
	//消费方  接受数据
	@Test
	public void testSpringMqConsumer() throws Exception{
		//初始化容器  配置的监听类就会监听到queue信息
	/*	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		System.in.read();*/
	}

}
