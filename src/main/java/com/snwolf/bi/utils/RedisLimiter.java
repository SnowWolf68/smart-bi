package com.snwolf.bi.utils;

import com.snwolf.bi.exception.RateLimitException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

public class RedisLimiter {
    private RedissonClient redissonClient;

    public RedisLimiter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void doLimit(String key, RateType rateType, long rate, long rateInterval, RateIntervalUnit rateIntervalUnit){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, rateInterval, rateIntervalUnit);

        boolean result = rateLimiter.tryAcquire();
        if(!result){
            throw new RateLimitException("访问过于频繁");
        }
    }
}
