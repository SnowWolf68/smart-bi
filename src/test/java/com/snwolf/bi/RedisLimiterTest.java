//package com.snwolf.bi;
//
//import com.snwolf.bi.utils.RedisLimiter;
//import com.snwolf.bi.utils.UserHolder;
//import org.junit.jupiter.api.Test;
//import org.redisson.api.RRateLimiter;
//import org.redisson.api.RateIntervalUnit;
//import org.redisson.api.RateType;
//import org.redisson.api.RedissonClient;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//
//@SpringBootTest
//public class RedisLimiterTest {
//
//    @Resource
//    private RedissonClient redissonClient;
//
//    @Test
//    void test1() throws InterruptedException {
//        RRateLimiter rateLimiter = redissonClient.getRateLimiter("1");
//        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
//
//        for (int i = 0; i < 3; i++) {
//            boolean result = rateLimiter.tryAcquire();
//            System.out.println(result);
//        }
//
//        Thread.sleep(1000);
//
//        for (int i = 0; i < 3; i++) {
//            boolean result = rateLimiter.tryAcquire();
//            System.out.println(result);
//        }
//    }
//
//    @Test
//    void test2() throws InterruptedException {
//        RedisLimiter redisLimiter = new RedisLimiter(redissonClient);
//
//        for (int i = 0; i < 6; i++) {
//            try {
//                redisLimiter.doLimit("testKey",
//                        RateType.OVERALL, 5L, 2L, RateIntervalUnit.SECONDS);
//                System.out.println(i);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }
//
//        Thread.sleep(2000);
//
//        for (int i = 0; i < 6; i++) {
//            try {
//                redisLimiter.doLimit("testKey",
//                        RateType.OVERALL, 5L, 2L, RateIntervalUnit.SECONDS);
//                System.out.println(i);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }
//}
