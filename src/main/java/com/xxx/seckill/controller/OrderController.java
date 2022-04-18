package com.xxx.seckill.controller;


import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IOrderService;
import com.xxx.seckill.vo.OrderDetailVo;
import com.xxx.seckill.vo.RespBean;
import com.xxx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    IOrderService service;

    @RequestMapping("detail")
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if(user == null){

            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        OrderDetailVo detail = service.detail(orderId);
        return  RespBean.success(detail);
    }

}
