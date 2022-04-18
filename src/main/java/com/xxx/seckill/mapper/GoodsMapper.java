package com.xxx.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.seckill.pojo.Goods;
import com.xxx.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoById(Long goodsId);
}
