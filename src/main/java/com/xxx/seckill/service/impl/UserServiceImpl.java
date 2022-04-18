package com.xxx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.seckill.mapper.UserMapper;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IUserService;
import com.xxx.seckill.utils.*;
import com.xxx.seckill.vo.LoginVo;
import com.xxx.seckill.vo.RespBean;
import com.xxx.seckill.vo.RespBeanEnum;
import com.xxx.seckill.vo.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pty
 * @since 2022-04-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String password = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(!StringUtils.hasText(password) || !StringUtils.hasText(mobile)){
            return  RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        /*//验证手机号码
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        //查找用户
        User user = userMapper.selectById(mobile);
        if(user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
       //将用户输入的密码进行加密后，和数据库中的密码进行比对
        if(!MD5Util.fromPassToDBPass(password,user.getSlat()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        UserThreadLocal.set(user);
        //生成cookie
        String ticket = UUIDUtil.uuid();

        redisUtil.set("user:"+ticket,user);

        CookieUtil.setCookie(request,response,"userTicket",ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        User user = (User)redisUtil.get("user:" + userTicket);
        if(user!=null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;

    }

}
