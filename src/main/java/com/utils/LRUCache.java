package com.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LRU缓存
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends HashMap {
    /* 锁 */
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /* list 用来顺序存储键 */
    private LinkedList<K> list;

    /* 缓存容量 */
    private int cacheCapacity;

    /* 已缓存的键数量 */
    private int size;

    /* 头结点 */
    private K head;

    /* 尾节点 */
    private K tail;

    /**
     * 构造函数
     * @param cacheCapacity 缓存键值对的数量
     */
    public LRUCache(int cacheCapacity){
        super(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.list = new LinkedList<K>();
        size = 0;
    }

    /**
     * 根据键获取值
     * @param key 键
     * @return V 值
     */
    @SuppressWarnings("unchecked")
    public V getCache(K key){
        return (V) this.get(key);
    }

    /**
     * 将一个键值对添加到缓存中
     * @param key 键
     * @param value 值
     */
    public void addToCache(K key, V value){
        Lock rL = lock.writeLock();
        rL.lock();
        if(list.contains(key)){
            list.remove(key);
            size--;
        }
        put(key, value);
        list.addFirst(key);
        head = key;
        size++;
        if (size == cacheCapacity) {
            deleteLast(tail);
        }
        rL.unlock();
    }

    /**
     * 获取键值对的数量
     * @return size
     */
    public int getKeysize(){
        return size;
    }

    /**
     * 删除最旧的键值
     * @param key 键
     */
    private void deleteLast(K key){
        this.remove(key);
        list.remove(key);
        size--;
        int index = list.size() - 1;
        tail = index > -1 ? list.get(index) : null;
    }
}
