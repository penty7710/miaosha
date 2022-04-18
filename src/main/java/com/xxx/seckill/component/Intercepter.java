package com.xxx.seckill.component;

import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IUserService;
import com.xxx.seckill.utils.CookieUtil;
import com.xxx.seckill.vo.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * Created by 彭天怡 2022/4/14.
 */
@Slf4j
@Component
public class Intercepter implements HandlerInterceptor {
    @Autowired
    IUserService iUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //先获取到userTicket的值
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        log.info("{}",userTicket);
        log.info("{}","进来了");

        if(!StringUtils.hasText(userTicket)){
            return false;
        }
        //从数据库查找用户
        User user = iUserService.getUserByCookie(userTicket, request, response);
        UserThreadLocal.set(user);
        return true;
    }
}
