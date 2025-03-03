package com.tining.demonmarket.gui.bean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private static final ConcurrentHashMap<String, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(String key) {
        return LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
