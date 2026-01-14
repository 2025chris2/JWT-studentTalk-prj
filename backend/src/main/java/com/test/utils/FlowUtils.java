package com.test.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {

    @Autowired
    StringRedisTemplate template;

    //blockTime是冷却时间！
    public boolean limitOnceCheck(String key, int blockTime){
        if(template.hasKey(key)){
            return false;
        }else{
            template.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
