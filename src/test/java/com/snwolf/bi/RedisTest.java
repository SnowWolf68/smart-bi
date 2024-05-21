//package com.snwolf.bi;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@SpringBootTest
//@Slf4j
//public class RedisTest {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    void redisTest(){
//        stringRedisTemplate.opsForValue().set("testKey", "testValue");
//        String result = stringRedisTemplate.opsForValue().get("testKey");
//        log.info("result: {}", result);
//    }
//}
