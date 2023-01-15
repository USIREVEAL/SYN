package com.usi.ch.syn.core.utils;

import lombok.Getter;

@Getter
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
