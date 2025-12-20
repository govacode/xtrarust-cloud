package com.xtrarust.cloud.redis.lock;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;

    public RedisDistributedLock(RedissonClient redisson) {
        this.redissonClient = redisson;
    }

    public boolean tryLockRun(String lockKey, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                runnable.run();
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, TimeUnit unit) {
        if (waitTime <= 0) {
            throw new IllegalArgumentException("waitTime must be greater than 0");
        }
        RLock lock = redissonClient.getLock(lockKey);
        boolean flag = false;
        try {
            flag = lock.tryLock(waitTime, unit);
            if (!flag) {
                return false;
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
        return true;
    }

    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, long leaseTime, TimeUnit unit) {
        Assert.isTrue(waitTime > 0, "waitTime must be greater than 0");
        Assert.isTrue(leaseTime > 0, "leaseTime must be greater than 0");
        RLock lock = redissonClient.getLock(lockKey);
        boolean flag = false;
        try {
            flag = lock.tryLock(waitTime, leaseTime, unit);
            if (!flag) {
                return false;
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
        return true;
    }

    public void lockRun(String lockKey, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void lockRun(String lockKey, Runnable runnable, long leaseTime, TimeUnit unit) {
        Assert.isTrue(leaseTime > 0, "leaseTime must be greater than 0");
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}
