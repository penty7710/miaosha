package com.xxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.seckill.pojo.Order;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.vo.GoodsVo;
import com.xxx.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);


    //订单详情
    OrderDetailVo detail(Long orderId);

    //获取秒杀地址
    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptche(User user, Long goodsId, String captcha);
}
