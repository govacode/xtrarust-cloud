package com.xtrarust.cloud.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 */
public interface DistributedLock {

    /**
     * 尝试获取分布式锁，获取到锁后执行 runnable.run() 方法，然后释放锁，返回true；若无法获取锁，则立即返回false，不会阻塞
     * <ul>
     * <li>获取锁的过程不会阻塞</li>
     * <li>锁会自动续期</li>
     * </ul>
     *
     * @param lockKey  上锁的key
     * @param runnable 需要执行的业务
     * @return 获取锁是否成功
     */
    boolean tryLockRun(String lockKey, Runnable runnable);

    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后执行 runnable.run() 方法，然后释放锁，返回true；否则返回false；此方法最多阻塞时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阻塞时长由 waitTime 指定</li>
     * <li>锁会自动续期</li>
     * </ul>
     *
     * @param lockKey  上锁的key
     * @param runnable 需要执行的业务
     * @param waitTime 获取锁等待时间，必须大于 0
     * @param unit     时间单位
     * @return 获取锁是否成功
     */
    boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, TimeUnit unit);

    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后执行 runnable.run() 方法，然后释放锁，返回true；否则返回false；此方法最多阻塞时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阻塞时长由 waitTime 指定</li>
     * <li>超过了 leaseTime，若锁还未释放，则自动释放</li>
     * </ul>
     *
     * @param lockKey   上锁的key
     * @param runnable  需要执行的业务
     * @param waitTime  获取锁等待时间，必须大于 0
     * @param leaseTime 锁持有时间（必须大于 0：超过了这个时间，若未主动释放，则会自动释放）
     * @param unit      时间单位
     * @return 获取锁是否成功
     * @throws InterruptedException
     */
    boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * 此方法会一直等待，直到获取锁后，获取到锁后执行 runnable.run() 方法，然后释放锁
     * <ul>
     * <li>获取锁的过程会阻塞，直到成功获取锁</li>
     * <li>锁会自动续期</li>
     * </ul>
     *
     * @param lockKey  上锁的key
     * @param runnable 需要执行的业务
     */
    void lockRun(String lockKey, Runnable runnable);

    /**
     * 此方法会一直等待，直到获取锁后，获取到锁后执行 runnable.run() 方法，然后释放锁
     * <ul>
     * <li>获取锁的过程会阻塞，直到成功获取锁</li>
     * <li>超过了 leaseTime，若锁还未释放，则自动释放</li>
     * </ul>
     *
     * @param lockKey   上锁的key
     * @param runnable  需要执行的业务
     * @param leaseTime 锁持有时间（必须大于 0：超过了这个时间，若未主动释放，则会自动释放）
     * @param unit      时间单位
     */
    void lockRun(String lockKey, Runnable runnable, long leaseTime, TimeUnit unit);
}
