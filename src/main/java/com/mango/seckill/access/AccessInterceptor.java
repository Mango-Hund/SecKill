package com.mango.seckill.access;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mango.seckill.pojo.SeckillUser;
import com.mango.seckill.service.ISeckillUserService;
import com.mango.seckill.vo.RespBean;
import com.mango.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.alibaba.fastjson.JSON;


@Service
@AllArgsConstructor
public class AccessInterceptor implements AsyncHandlerInterceptor {

    private final ISeckillUserService seckillUserService;

    private final RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(handler instanceof HandlerMethod) {
            SeckillUser user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin) {
                if(user == null) {
                    render(response, RespBean.error(RespBeanEnum.LOG_IN_AGAIN));
                    return false;
                }
                key += user.getId();
            }else {
                //do nothing
            }
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if(count  == null) {
                redisTemplate.opsForValue().set(key, 1);
            }else if(count < maxCount) {
                redisTemplate.opsForValue().increment(key);
            }else {
                render(response,  RespBean.error(RespBeanEnum.LOG_IN_AGAIN));
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, RespBean cm)throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(cm);
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter("token");
        String cookieToken = getCookieValue(request, "token");
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return seckillUserService.getUserByCookie(token, request, response);
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

