package com.xxx.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.wf.captcha.ArithmeticCaptcha;
import com.xxx.seckill.exception.GlobalException;
import com.xxx.seckill.pojo.Order;
import com.xxx.seckill.pojo.SeckillMessage;
import com.xxx.seckill.pojo.SeckillOrder;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.rabbimq.MQSender;
import com.xxx.seckill.service.IGoodsService;
import com.xxx.seckill.service.IOrderService;
import com.xxx.seckill.service.ISeckillOrderService;
import com.xxx.seckill.utils.RedisUtil;
import com.xxx.seckill.vo.GoodsVo;
import com.xxx.seckill.vo.RespBean;
import com.xxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 * windows 优化前 585
 *     缓存 优化 706
 *     优化qps   2235
 * linux 优化前 289
 *
 * 解决超卖：1.为用户id和orderid建立联合的唯一索引，防止order表数据超过商品数量
 *         2.修改商品数量的时候需要判断商品的数量是否大于0，大于0进行修改并且添加订单记录，否则直接返回
 *         3.判断的时候利用单条语句的原子性进行操作
 *
 * Created by 彭天怡 2022/4/14.
 */
@Slf4j
@Configuration
@RequestMapping("/seckill")
//sping启动后，初始化Bean时，若该bean实现InitializingBean接口，会自动调用afterPropertiesSet()方法，完成用户自定义的初始化操作
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService service;
    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/{path}/doSeckill",method =  RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(Long goodsId, User user,@PathVariable String path){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check){
            return  RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }


        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisUtil.get("user:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPATE_ERROR);
        }

        //通过内存标记，减少redis的访问，在初始化容器的时候，将商品数量存入redis，并设置一个map来保存每个商品的状态
        //当商品库存为0时，将状态设置为true，之后每次进来判断状态，如果
        if (EmptyStockMap.get(goodsId)){
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //预减库存
        long stock = redisUtil.decr("seckillGoods:" + goodsId, 1);
        if(stock < 0){
            EmptyStockMap.put(goodsId,true);
            redisUtil.incr("seckillGoods:"+goodsId,1);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);

        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));

        return RespBean.success(0);



        /*
        GoodsVo goodsVo = service.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goodsVo.getStockCount() < 1){

            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断是否重复抢购
        //SeckillOrder seckillOrder = seckillOrderService.findById(user.getId(), goodsVo.getId());
        SeckillOrder seckillOrder = (SeckillOrder) redisUtil.get("user:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
           return RespBean.error(RespBeanEnum.REPATE_ERROR);
        }

        Order order = orderService.seckill(user,goodsVo);

        return RespBean.success(order);
        */
    }

    /**
     *获取秒杀地址
     */
    @GetMapping("/path")
    @ResponseBody
    public RespBean getPath(User user,Long goodsId,String captcha){
        if(user == null){
            return  RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptche(user,goodsId,captcha);
        if(!check){
            return  RespBean.error(RespBeanEnum.ERROR_CAPTCHE);
        }

        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);

    }

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0) {
             throw  new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        response.setContentType("image/img");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);

        //生成验证码，将结果放入到redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);

        redisUtil.set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败");
        }
    }






    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long result = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(result);
    }


    //初始化容器的时候执行的方法
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = service.findGoodsVo();
        if(list.isEmpty()){
            return;
        }
        //在初始化容器的时候，将数据放到redis里面，直接在redis进行数量的增减
        //数据预热
        list.forEach(goodsVo ->{
            redisUtil.set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });

    }
}
