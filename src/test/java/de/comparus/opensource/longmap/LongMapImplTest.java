package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class LongMapImplTest {
    private LongMapImpl<String> longMap = new LongMapImpl<>();

    @Test
    public void testPutEmptyMap() {
        assertNull(longMap.put(1, "str1"));
        assertEquals(longMap.size(), 1);
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsValue("str1"));
    }

    @Test
    public void testPutNotEmptyMapNewValue() {
        assertNull(longMap.put(1, "str1"));
        assertNull(longMap.put(2, "str2"));
        assertEquals(longMap.size(), 2);
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsValue("str1"));
        assertTrue(longMap.containsKey(2));
        assertTrue(longMap.containsValue("str2"));
    }

    @Test
    public void testPutNotEmptyMapReplaceValue() {
        assertNull(longMap.put(1, "str1"));
        assertEquals("str1",longMap.put(1, "str2"));
        assertEquals(longMap.size(), 1);
        assertTrue(longMap.containsKey(1));
        assertFalse(longMap.containsValue("str1"));
        assertTrue(longMap.containsValue("str2"));
    }

    @Test
    public void testPutInOneBucket() {
        assertNull(longMap.put(1, "str1"));
        assertNull(longMap.put(9, "str9"));
        assertNull(longMap.put(17, "str17"));
        assertEquals(longMap.size(), 3);
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsKey(9));
        assertTrue(longMap.containsKey(17));
        assertTrue(longMap.containsValue("str1"));
        assertTrue(longMap.containsValue("str9"));
        assertTrue(longMap.containsValue("str17"));
    }

    @Test
    public void testPutNegativeKey() {
        assertNull(longMap.put(9, "str9"));
        assertNull(longMap.put(-9, "str-9"));
        assertEquals(longMap.size(), 2);
        assertTrue(longMap.containsKey(9));
        assertTrue(longMap.containsKey(-9));
        assertTrue(longMap.containsValue("str9"));
        assertTrue(longMap.containsValue("str-9"));
    }

    @Test
    public void testPutAndGetNullValue() {
        assertNull(longMap.put(1, null));
        assertTrue(longMap.containsKey(1));
        assertNull(longMap.get(1));
    }

    @Test
    public void testContainsKeyTrue() {
        assertNull(longMap.put(1, "str1"));
        assertTrue(longMap.containsKey(1));
    }

    @Test
    public void testContainsKeyFalse() {
        assertFalse(longMap.containsKey(1));
    }

    @Test
    public void testContainsValueTrue() {
        assertNull(longMap.put(1, "str1"));
        assertTrue(longMap.containsValue("str1"));
    }

    @Test
    public void testContainsValueFalse() {
        assertFalse(longMap.containsValue("str1"));
    }

    @Test
    public void testGet() {
        assertNull(longMap.put(1, "str1"));
        assertEquals(longMap.get(1), "str1");
    }

    @Test
    public void testGetEmptyMap() {
        assertNull(longMap.get(1));
    }

    @Test
    public void testGetNotEmptyMapElementDoesNotExist() {
        assertNull(longMap.put(1, "str1"));
        assertNull(longMap.get(0));
    }

    @Test
    public void testIsEmptyTrue() {
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testIsEmptyFalse() {
        assertNull(longMap.put(1, "str1"));
        assertFalse(longMap.isEmpty());
    }

    @Test
    public void testRemoveEmptyMap() {
        assertNull(longMap.remove(0));
    }

    @Test
    public void testRemoveValidKey() {
        assertNull(longMap.put(1, "str1"));
        assertEquals(longMap.size(), 1);
        assertEquals("str1", longMap.remove(1));
        assertEquals(longMap.size(), 0);
    }

    @Test
    public void testRemoveInvalidKey() {
        assertNull(longMap.remove(2));
    }

    @Test
    public void testClearEmptyMap() {
        longMap.clear();
        assertEquals(longMap.size(), 0);
    }

    @Test
    public void testClearNotEmptyMap() {
        longMap.put(1, "str1");
        longMap.put(2, "str2");
        assertEquals(longMap.size(), 2);
        longMap.clear();
        assertEquals(longMap.size(), 0);
        assertFalse(longMap.containsKey(1));
    }

    @Test
    public void testIncreaseCapacity() {
        LongMapImpl<String> smallLongMap = new LongMapImpl<>(3, 1);
        smallLongMap.put(5, "str1");
        smallLongMap.put(6, "str2");
        assertEquals(smallLongMap.size(), 2);
        smallLongMap.put(3, "str3");
        smallLongMap.put(4, "str4");
        assertEquals(smallLongMap.size(), 4);
        assertTrue(smallLongMap.containsKey(4));
        assertTrue(smallLongMap.containsValue("str4"));
    }

    @Test
    public void testIteratorHasNextFalseOnEmptyMap() {
        Iterator iterator = longMap.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterator() {
        longMap.put(1, "str1");
        longMap.put(2, "str2");
        longMap.put(3, "str3");

        Map<Long, String> hashMap = new HashMap<>();
        hashMap.put(1L, "str1");
        hashMap.put(2L, "str2");
        hashMap.put(3L, "str3");

        for (LongMapEntry<String> stringLongMapEntry : longMap) {
            long key = stringLongMapEntry.getKey();
            assertTrue(hashMap.containsKey(key));
            assertEquals(hashMap.remove(key)
                    , stringLongMapEntry.getValue());
        }
        assertTrue(hashMap.isEmpty());

    }

    @Test
    public void testGetKeys() {
        for (long i = 0; i < 3; i++) {
            longMap.put(i, "str" + i);
        }
        long[] keys = longMap.keys();
        assertEquals(3, keys.length);
        assertEquals(0L, keys[0]);
        assertEquals(1L, keys[1]);
        assertEquals(2L, keys[2]);
    }

    @Test
    public void testGetValues() {
        for (long i = 0; i < 3; i++) {
            longMap.put(i, "str" + i);
        }
        String[] values = longMap.values();
        assertEquals(3, values.length);
        assertEquals("str0", values[0]);
        assertEquals("str1", values[1]);
        assertEquals("str2", values[2]);
    }
}