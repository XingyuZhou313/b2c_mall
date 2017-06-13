package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${expire.time}")
	private Integer SESSION_EXPIRE;
	//动态查询数据库中是否已经存在  检验信息
	public E3Result checkUser(String data, Integer type) {
		TbUserExample example=new TbUserExample();
		// 3、根据不同的参数生成不同的查询条件
		// 1：用户名 2：手机号 3:邮箱
		Criteria criteria = example.createCriteria();
		if (type==1) {
			criteria.andUsernameEqualTo(data);
		}else if(type==2){
			criteria.andPhoneEqualTo(data);
		}else if(type==3){
			criteria.andEmailEqualTo(data);
		}else{
			return E3Result.build(400, "参数类型错误");
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if (list!=null&&list.size()>0) {
			//如果查到数据返回false
			return E3Result.ok(false);
		}
		//如果没有查询到返回true  可以进行下一步
		return E3Result.ok(true);
	}
	//注册新用户的实现
	public E3Result addUser(TbUser user) {
		//判断新用户的用户名和密码是否为空
		if (StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
			return E3Result.build(400, "用户名和密码不能为空!");
		}
		// 1：用户名 2：手机号 3:邮箱
		//判断要注册的用户名是否存在
		E3Result result = checkUser(user.getUsername(), 1);
		if (result.getStatus()!=200||(boolean)result.getData()!=true) {
			return E3Result.build(400, "用户名已存在!");
		}
		//判断要注册的用户的手机号是否存在
		result = checkUser(user.getPhone(), 2);
		if (result.getStatus()!=200||(boolean)result.getData()!=true) {
			return E3Result.build(400, "手机号已被注册!");
		}
		//判断要注册的用户的邮箱是否存在
		if (StringUtils.isNotBlank(user.getEmail())) {
			result = checkUser(user.getEmail(), 3);
			if (result.getStatus()!=200||(boolean)result.getData()!=true) {
				return E3Result.build(400, "邮箱已被注册!");
			}
		}
		//检验通过向数据库插入数据
		//完善用户数据
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//密码使用md5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return E3Result.ok(true);
	}
	//登陆功能的实现
	public E3Result login(TbUser user) {
		//获取用户名
		//查询数据库 是否存在该用户
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(user.getUsername());
		List<TbUser> list = userMapper.selectByExample(example);
		if (list==null||list.size()==0) {
			return E3Result.build(400, "用户名或密码错误!");
		}
		TbUser tbUser = list.get(0);
		//如果有 进行密码比对 
		if (!tbUser.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
			//比对不匹配返回 错误信息
			E3Result.build(400, "用户名或密码错误!");
		}
		//用户身份检验成功，则使用uuid生成唯一token 存入redis数据库
		String token = UUID.randomUUID().toString();
		//保护用户信息 将密码设为空
		tbUser.setPassword(null);
		jedisClient.hset("SESSION:"+token, "user", JsonUtils.objectToJson(tbUser));
		//设置过期时间  30分钟自动过期
		jedisClient.expire("SESSION:"+token, SESSION_EXPIRE);
		//返回E3Result 将token信息包装返回
		return E3Result.ok(token);
	}
	//根据token查询当前登录用户信息
	public E3Result getLoginUserByToken(String token) {
		//根据token查询用户信息
		String json = jedisClient.hget("SESSION:"+token, "user");
		//如果为空返回登录信息过期
		if (StringUtils.isBlank(json)) {
			return E3Result.build(400, "用户登陆信息已过期，请重新登陆!");
		}
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		//重新设置该对象的过期时间 
		jedisClient.expire("SESSION:"+token, SESSION_EXPIRE);
		//将查询到的用户信息转换为pojo包装到E3result对象中
		return E3Result.ok(user);
	}
	//用户登出
	public void logout(String token) {
		//将缓存信息删除
		jedisClient.hdel("SESSION:"+token, "user");
	}

}
