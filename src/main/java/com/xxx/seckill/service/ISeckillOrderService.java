package com.xxx.seckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.seckill.pojo.SeckillGoods;
import com.xxx.seckill.pojo.SeckillOrder;
import com.xxx.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    SeckillOrder findById(Long user_id, Long goods_id);


    Long getResult(User user, Long goodsId);
}
