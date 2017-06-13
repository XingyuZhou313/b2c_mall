package cn.e3mall.sso.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbUser;

public interface UserService {
	
	//检查注册信息是否存在
	E3Result checkUser(String data,Integer type);
	//注册新用户
	E3Result addUser(TbUser user);
	//登陆功能 
	E3Result login(TbUser user);
	//根据token信息查询当前登录人信息
	E3Result getLoginUserByToken(String token);
	void logout(String token);
}
