package com.xtrarust.cloud.redis.lock;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class RedisDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;

    public RedisDistributedLock(RedissonClient redisson) {
        this.redissonClient = redisson;
    }

    @Override
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

    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, TimeUnit unit) {
        Assert.isTrue(waitTime > 0, "waitTime must be greater than 0");
        RLock lock = redissonClient.getLock(lockKey);
        boolean flag = false;
        try {
            if (lock.tryLock(waitTime, unit)) {
                try {
                    runnable.run();
                    flag = true;
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, long leaseTime, TimeUnit unit) {
        Assert.isTrue(waitTime > 0, "waitTime must be greater than 0");
        RLock lock = redissonClient.getLock(lockKey);
        boolean flag = false;
        try {
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                try {
                    runnable.run();
                    flag = true;
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public void lockRun(String lockKey, Runnable runnable, Supplier<Boolean> businessCheckSupplier) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            if (businessCheckSupplier != null && Boolean.TRUE.equals(businessCheckSupplier.get())) {
                return;
            }
            runnable.run();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void lockRun(String lockKey, Runnable runnable, long leaseTime, TimeUnit unit, Supplier<Boolean> businessCheckSupplier) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
        try {
            if (businessCheckSupplier != null && Boolean.TRUE.equals(businessCheckSupplier.get())) {
                return;
            }
            runnable.run();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
