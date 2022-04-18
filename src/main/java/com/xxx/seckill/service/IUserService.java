package com.xxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.vo.LoginVo;
import com.xxx.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pty
 * @since 2022-04-13
 */
public interface IUserService extends IService<User> {

    //登录
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);


    /**
     * 根据cookie获取用户
     */
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);

}
