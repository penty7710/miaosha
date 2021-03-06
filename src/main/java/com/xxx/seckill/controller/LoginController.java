package com.xxx.seckill.controller;

import com.xxx.seckill.service.IUserService;
import com.xxx.seckill.vo.LoginVo;
import com.xxx.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by 彭天怡 2022/4/13.
 */
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * 跳转登录页面
     */
    @RequestMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录功能
     */
    @ResponseBody
    @RequestMapping("/doLogin")
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return userService.doLogin(loginVo,request,response);
    }



}
