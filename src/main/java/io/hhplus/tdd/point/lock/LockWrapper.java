package io.hhplus.tdd.point.lock;

import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

public class LockWrapper {
    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private volatile long lastAccessTime = System.currentTimeMillis();

    public void lock(){
        updateAccessTime();
        lock.lock();
    }

    public void unlock(){
        lock.unlock();
    }

    public boolean hasQueuedThreads() {
        return lock.hasQueuedThreads();
    }

    public void updateAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }
}
