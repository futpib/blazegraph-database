package com.bigdata.cache;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * No-operation always-empty cache. Mainly for tests, but for disabled caching too (never desirable probably).
 * 
 * @param <K>
 *            The generic type of the keys.
 * @param <V>
 *            The generic type of the values.
 */
public class ConcurrentWeakValueCacheNoop<K, V> implements IConcurrentWeakValueCache<K, V> {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public V get(K k) {
        return null;
    }

    @Override
    public boolean containsKey(K k) {
        return false;
    }

    @Override
    public V put(K k, V v) {
        return null;
    }

    @Override
    public V putIfAbsent(K k, V v) {
        return null;
    }

    @Override
    public V remove(K k) {
        return null;
    }

    @Override
    public Iterator<WeakReference<V>> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Iterator<Entry<K, WeakReference<V>>> entryIterator() {
        return Collections.emptyIterator();
    }
}
