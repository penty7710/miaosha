package com.xxx.seckill.rabbimq;

import com.alibaba.fastjson.JSON;
import com.xxx.seckill.pojo.SeckillMessage;
import com.xxx.seckill.pojo.SeckillOrder;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IGoodsService;
import com.xxx.seckill.service.IOrderService;
import com.xxx.seckill.utils.RedisUtil;
import com.xxx.seckill.vo.GoodsVo;
import com.xxx.seckill.vo.RespBean;
import com.xxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 彭天怡 2022/4/18.
 */
@Slf4j
@Component
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 下单操作
     */
    @RabbitListener(queues = "seckillqueue")
    public void receive(String message){
        //将消息转成原来的对象
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);

        Long goodsId = seckillMessage.getGoodsId();

        User user = seckillMessage.getUser();
        //判断数据库中商品的库存， 如果商品数量小于1，直接返回
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1){
            return;
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisUtil.get("user:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return;
        }

        //下单操作
        iOrderService.seckill(user,goodsVo);

    }
}
