package com.xxx.seckill.rabbimq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq消息发送方
 * Created by 彭天怡 2022/4/18.
 */
@Slf4j
@Component
public class MQSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     */
    public void sendSeckillMessage(String message){
        log.info("发送消息"+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
