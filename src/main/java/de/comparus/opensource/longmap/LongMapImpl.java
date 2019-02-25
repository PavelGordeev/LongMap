package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V>, Iterable<LongMapEntry<V>> {
    private final static int DEFAULT_INITIAL_CAPACITY = 8;
    private final static double DEFAULT_LOAD_FACTOR = 0.75;

    private double loadFactor;
    private Entry<V>[] buckets;
    private int size;

    public LongMapImpl() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public LongMapImpl(int capacity, double loadFactor) {
        this.loadFactor = loadFactor;
        buckets = new Entry[capacity];
    }

    public V put(long key, V value) {
        if (size >= loadFactor * buckets.length) {
            increaseCapacity();
        }

        Entry<V> entryToUpdate = getEntry(key);
        if (entryToUpdate != null) {
            V oldValue = entryToUpdate.value;
            entryToUpdate.value = value;
            return oldValue;
        }

        int index = getBucketIndex(key);
        Entry<V> newEntry = new Entry<>(key, value);

        innerPut(index, newEntry, buckets);
        size++;
        return null;
    }

    public V get(long key) {
        Entry<V> currentEntry = getEntry(key);
        if (currentEntry != null) {
            return currentEntry.value;
        }
        return null;
    }

    public V remove(long key) {
        for (Iterator<LongMapEntry<V>> iterator = iterator(); iterator.hasNext(); ) {
            Entry<V> entry = (Entry<V>) iterator.next();
            if (entry.key == key) {
                iterator.remove();
                size--;
                return entry.value;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        return getEntry(key) != null;
    }

    public boolean containsValue(V value) {
        for (LongMapEntry<V> entry : this) {
            if (Objects.equals(value, entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public long[] keys() {
        long[] keys = new long[size];
        int index = 0;

        for (LongMapEntry<V> entry : this) {
            keys[index] = entry.getKey();
            index++;
        }

        return keys;
    }

    public V[] values() {
        // cannot create empty array of unknown type.
        if (isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        V[] values = (V[]) Array.newInstance(iterator().next().getValue().getClass(), size);

        int index = 0;
        for (LongMapEntry<V> entry : this) {
            values[index] = entry.getValue();
            index++;
        }

        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
        size = 0;
    }

    private void increaseCapacity() {
        int newCapacity = buckets.length * 2;

        @SuppressWarnings("unchecked")
        Entry<V>[] newBuckets = new Entry[newCapacity];

        for (LongMapEntry<V> entry : this) {
            int newIndex = getBucketIndex(entry.getKey(), newCapacity);
            innerPut(newIndex, (Entry<V>) entry, newBuckets);
        }

        buckets = newBuckets;
    }

    private int getBucketIndex(long key) {
        return getBucketIndex(key, buckets.length);
    }

    private int getBucketIndex(long key, int bucketCount) {
        return (int) Math.abs(key % bucketCount);
    }

    private Entry<V> getEntry(long key) {
        for (LongMapEntry<V> entry : this) {
            if (entry.getKey() == key) {
                return (Entry<V>) entry;
            }
        }
        return null;
    }

    private void innerPut(int index, Entry<V> newEntry, Entry<V>[] buckets) {
        if (buckets[index] == null) {
            buckets[index] = newEntry;
        } else {
            Entry<V> currentEntry = buckets[index];
            while (currentEntry.next != null) {
                currentEntry = currentEntry.next;
            }
            currentEntry.next = newEntry;
        }
    }

    @Override
    public Iterator<LongMapEntry<V>> iterator() {
        return new LongMapIterator();
    }

    private class LongMapIterator implements Iterator<LongMapEntry<V>> {

        private int count;

        private int bucketIndex = -1;
        private Entry<V> currentEntry;
        private Entry<V> prevEntry;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public Entry<V> next() {
            if (!hasNext()) {
                throw new IllegalStateException("No next element!");
            }

            if (count == 0 || currentEntry.next == null) {

                // not sure that inline is most readable approach here :)
                while (buckets[++bucketIndex] == null) ;

                currentEntry = buckets[bucketIndex];
                prevEntry = null;
            } else {
                prevEntry = currentEntry;
                currentEntry = currentEntry.next;
            }

            count++;
            return currentEntry;
        }

        public void remove() {
            if (prevEntry == null) {
                buckets[bucketIndex] = null;
            } else {
                prevEntry.next = null;
            }
            count--;
        }
    }

    private static class Entry<E> implements LongMapEntry<E> {
        private long key;
        private E value;
        private Entry<E> next;

        private Entry(long key, E value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public long getKey() {
            return key;
        }

        @Override
        public E getValue() {
            return value;
        }
    }
}