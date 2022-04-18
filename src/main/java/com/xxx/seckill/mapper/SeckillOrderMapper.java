package com.xxx.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.seckill.pojo.SeckillOrder;

/**
 * <p>
 * 秒杀订单表 Mapper 接口
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    SeckillOrder findByid(Long user_id, Long goods_id);
}
