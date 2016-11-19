package com.example.xiongcen.myapplication.network.cache;

/**
 * Created by xiongcen on 16/11/18.
 */

public interface Cache<K,V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);
}
