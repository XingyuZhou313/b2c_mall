package cn.e3mall.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	
	@Test
	public void jedisTest(){
		//获取jedis对象
		Jedis jedis = new Jedis("192.168.25.128", 6379);
		jedis.set("test", "abcd");
		String string = jedis.get("test");
		System.out.println(string);
		jedis.close();
	}
	//jedis连接池
	@Test
	public void jedisPoolTest(){
		//创建一个jedispool
		JedisPool jedisPool = new JedisPool("192.168.25.128", 6379);
		//从池中获取jedis对象
		Jedis jedis = jedisPool.getResource();
		
		String string = jedis.get("test");
		System.out.println(string);
		jedis.close();
		jedisPool.close();
	}
	
	//连接redis集群的方式
	@Test
	public void testJedisCluster(){
		Set<HostAndPort> nodes=new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		//创建jediscluster对象
		JedisCluster jedisCluster = new JedisCluster(nodes);
		jedisCluster.set("testCluster", "cluster");
		String string = jedisCluster.get("testCluster");
		System.out.println(string);
		jedisCluster.close();
	}
	
	
	//spring配置文件提供的
	@Test
	public void test(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		//测试集群版
		String string = jedisClient.get("testCluster");
		//测试单机版
		/*String string = jedisClient.get("test");*/
		System.out.println(string);
	}
}
