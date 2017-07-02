package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 判断是有登陆拦截器的实现
 * @author Administrator
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private UserService userService;
	@Value("${COOKE_TOKEN_KEY}")
	private String COOKE_TOKEN_KEY;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		//根据token信息获取cookie中的登陆的用户的信息
		String token = CookieUtils.getCookieValue(request,COOKE_TOKEN_KEY,true);
		if (StringUtils.isBlank(token)) {
			//如果没有登陆信息  放行
			return true;
		}
		//获取到登录用户的信息，查询redis中的登陆信息是否过期
		E3Result result = userService.getLoginUserByToken(token);
		if (result.getStatus()!=200) {
			return true;
		}
		TbUser user = (TbUser) result.getData();
		//如果有登陆信息并且没有过期 将用户信息存到request对象中 并放行
		request.setAttribute("user", user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
