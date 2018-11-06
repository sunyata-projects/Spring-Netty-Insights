package org.sunyata.quark.server.springcloud.lock;

import org.redisson.api.RLock;
import org.sunyata.quark.lock.BusinessLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by leo on 17/7/31.
 */
public class RedisLock implements BusinessLock {
    private RLock lock;

    public RedisLock(RLock lock){
        this.lock = lock;
    }
    @Override
    public void acquire() throws Exception {
        lock.lock();
    }

    @Override
    public boolean acquire(long l, TimeUnit timeUnit) throws Exception {
        boolean b = lock.tryLock(l, timeUnit);
        return b;
    }

    @Override
    public void release() throws Exception {
        lock.unlock();
    }
}
