package com.snwolf.bi;

import org.junit.jupiter.api.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisLimiterTest {

    @Resource
    private RedissonClient redissionClient;

    @Test
    void test() throws InterruptedException {
        RRateLimiter rateLimiter = redissionClient.getRateLimiter("1");
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        for (int i = 0; i < 3; i++) {
            boolean result = rateLimiter.tryAcquire();
            System.out.println(result);
        }

        Thread.sleep(1000);

        for (int i = 0; i < 3; i++) {
            boolean result = rateLimiter.tryAcquire();
            System.out.println(result);
        }
    }
}
