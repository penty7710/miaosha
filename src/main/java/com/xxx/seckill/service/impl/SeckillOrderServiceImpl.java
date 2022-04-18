package com.xxx.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.seckill.mapper.SeckillOrderMapper;
import com.xxx.seckill.pojo.SeckillOrder;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.ISeckillOrderService;
import com.xxx.seckill.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public SeckillOrder findById(Long user_id, Long goods_id) {
        return  seckillOrderMapper.findByid(user_id,goods_id);
    }

    /**
     *返回OrderId表示成功
     * -1表示失败
     * 0表示排队中
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().
                eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(null != seckillOrder){
            return seckillOrder.getOrderId();
        }else if(redisUtil.hasKey("isStockEmpty:"+goodsId)) {
            return  -1L;
        }else {
            return 0L;
        }
    }
}
