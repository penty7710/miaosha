package com.xxx.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.xml.ws.Action;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DefaultRedisScript  script;

    @Test
    void contextLoads() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String s = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", s, 5, TimeUnit.SECONDS);
        if(isLock){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name:"+name);
            System.out.println(valueOperations.get("k1"));
            Boolean resul = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), s);
        }

    }

}
