package cn.e3mall.order.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ctc.wstx.util.StringUtil;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 用户登陆  拦截器
 * @author Administrator
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserService userService;
	@Autowired
	private CartService cartService;
	@Value("${COOKE_TOKEN_KEY}")
	private String COOKE_TOKEN_KEY;
	@Value("${login.url}")
	private String loginUrl;
	@Value("${COOKE_CART_KEY}")
	private String cartKey;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		//判断是否有token 没有token 跳转到登陆页面
		String token = CookieUtils.getCookieValue(request, COOKE_TOKEN_KEY, true);
		//判断token信息所对应的用户信息 是否存在
		if (StringUtils.isBlank(token)) {
			//没有登陆信息
			//进行页面的跳转 
			response.sendRedirect(loginUrl+"/page/login?redirect="+request.getRequestURL());
			//进行拦截
			return false;
		}
		E3Result result = userService.getLoginUserByToken(token);
		//判断用户信息是否已经登陆过期  过期  跳转到登陆界面
		if (result.getStatus()!=200) {
			//登陆信息已过期  已失效
			//进行页面的跳转 
			response.sendRedirect(loginUrl+"/page/login?redirect="+request.getRequestURL());
			//进行拦截
			return false;
		}
		TbUser user = (TbUser) result.getData();
		//将用户信息存入request
		request.setAttribute("user", user);
		//判断cooke中是否有购物车信息  如果有购物车信息  则与 该用户的购物车信息进行合并
		String json = CookieUtils.getCookieValue(request, cartKey, true);
		//如果json中存在购物车信息 则进行合并
		if (StringUtils.isNotBlank(json)) {
			List<TbItem> cartList = JsonUtils.jsonToList(json, TbItem.class);
			//合并购物车
			cartService.mergeCartList(cartList, user.getId());
			//删除cookie中的购物车信息
			CookieUtils.deleteCookie(request, response, cartKey);
		}
		//放行
		return true;
	}


	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
