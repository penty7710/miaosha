package com.xxx.seckill.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.xxx.seckill.pojo.User;
import com.xxx.seckill.service.IGoodsService;
import com.xxx.seckill.service.IUserService;
import com.xxx.seckill.utils.RedisUtil;
import com.xxx.seckill.vo.DetailVo;
import com.xxx.seckill.vo.GoodsVo;
import com.xxx.seckill.vo.RespBean;
import com.xxx.seckill.vo.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;


/**
 * Created by 彭天怡 2022/4/13.
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    IUserService iUserService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private ThymeleafViewResolver  thymeleafViewResolver;

    /**
     * windows 优化前qps：1596
     *          缓存qps: 4170
     * linux 优化前qps：521
     *
     *
     * 跳转到商品列表页
     */
    @RequestMapping(value = "/toList",produces = "text/html;chareset=utf-8")
    @ResponseBody
    public String toList(Model model,HttpServletResponse response,HttpServletRequest request){
        //从Redis中获取页面，如果不为空，直接返回页面
        String html = (String)redisUtil.get("goodsList");
        if(StringUtils.hasText(html)){
            return html;
        }


        model.addAttribute("goodsList",goodsService.findGoodsVo());

        //如果为空，手动渲染，存入Redis返回
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale()
                ,model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);

        if(StringUtils.hasText(html)){
            redisUtil.set("goodsList",html,60);
        }

        return html;
    }

    /**
     * 跳转商品详情信息
     */
    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(@PathVariable Long goodsId,Model model,User user,HttpServletRequest request,HttpServletResponse response){

        //从redis中获取页面，如果不为空直接返回
        String html= (String) redisUtil.get("goodsDetail:" + goodsId);
        if(StringUtils.hasText(html)){
            return  html;
        }

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date date = new Date();
        int seckillStatus =0;
        int remainSeconds = 0;
        if(date.before(startDate)){
            remainSeconds = (int)(startDate.getTime()-date.getTime())/1000;
        }else if(date.after(endDate)){
            seckillStatus =2;
            remainSeconds =-1;
        }else{
            seckillStatus =1;
            remainSeconds = 0;
        }
        log.info("{}",user);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",seckillStatus);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("user",user);

        //如果为空，手动渲染，存入Redis返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(StringUtils.hasText(html)){
            redisUtil.set("goodsDetail:"+goodsId,html,60);
        }

        return html;
    }

    /**
     * 跳转商品详情信息
     */
    @RequestMapping( "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(@PathVariable Long goodsId,  User user){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date date = new Date();
        int seckillStatus =0;
        int remainSeconds = 0;
        if(date.before(startDate)){
            remainSeconds = (int)(startDate.getTime()-date.getTime())/1000;
        }else if(date.after(endDate)){
            seckillStatus =2;
            remainSeconds =-1;
        }else{
            seckillStatus =1;
            remainSeconds = 0;
        }

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(seckillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        log.info("{}",detailVo);
        return RespBean.success(detailVo);
    }
}
