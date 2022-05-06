package com.xxx.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.seckill.mapper.OrderMapper;
import com.xxx.seckill.pojo.Order;
import com.xxx.seckill.pojo.SeckillGoods;
import com.xxx.seckill.pojo.SeckillOrder;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IOrderService;
import com.xxx.seckill.service.ISeckillGoodsService;
import com.xxx.seckill.service.ISeckillOrderService;
import com.xxx.seckill.utils.MD5Util;
import com.xxx.seckill.utils.RedisUtil;
import com.xxx.seckill.utils.UUIDUtil;
import com.xxx.seckill.vo.GoodsVo;
import com.xxx.seckill.vo.OrderDetailVo;
import com.xxx.seckill.vo.RespBeanEnum;
import com.xxx.seckill.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pty
 * @since 2022-04-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService service;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private GoodsServiceImpl goodsService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        SeckillGoods goods = service.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        goods.setStockCount(goods.getStockCount()-1);
        //只有当stock_count大于0的时候才能够进行修改
        //通过在修改库存的时候，比较库存的值是否大于0来解决超卖问题
        boolean update = service.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1")
                .eq("goods_id",goods.getId()).gt("stock_count",0));

        if(goods.getStockCount()<1){

            redisUtil.set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }


        //生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setStatus(0);
        order.setOrderChannel(1);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);

        //将订单存入到redis中
        redisUtil.set("order:"+user.getId()+":"+goods.getId(),seckillOrder);

        return order;

    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);

        GoodsVo goodsVoByGoodsId = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();

        detailVo.setGoodsVo(goodsVoByGoodsId);
        detailVo.setOrder(order);

        return detailVo;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String s = MD5Util.md5(UUIDUtil.uuid() + "123456");
        //将生成的paht缓存到redis中，设置60秒的过期时间
        redisUtil.set("seckillPath:"+user.getId()+":"+goodsId,s,60);
        return s;
    }

    //校验秒杀地址
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user == null || goodsId<0 || !StringUtils.hasText(path)){
            return false;
        }
        String s = (String) redisUtil.get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(s);
    }

    //校验验证码
    @Override
    public boolean checkCaptche(User user, Long goodsId, String captcha) {
        if(!StringUtils.hasLength(captcha) || user == null || goodsId<0){
            return false;
        }
        String s = (String) redisUtil.get("captcha:" + user.getId() + ":" + goodsId);

        if (!captcha.equals(s)) {
            return  false;
        } else {
            return true;
        }

    }
}
