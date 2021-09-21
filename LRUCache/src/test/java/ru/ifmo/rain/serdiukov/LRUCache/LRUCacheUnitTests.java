package ru.ifmo.rain.serdiukov.LRUCache;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class LRUCacheUnitTests {

    private static final List<Object> TEST_OBJECTS = List.of(
            "Some string",
            1,
            new Object(),
            "Another string",
            5.5d,
            List.of(6, 7, 8),
            Map.of(0, "0", 1, "1")
    );

    @Test
    public void testEmptyCache() {
        final LRUCache<?, ?> cache = new LRUCacheImpl<>(4);
        assertThat("empty cache isEmpty()", cache.isEmpty(), is(equalTo(true)));
        assertThat("empty cache has size 0", cache.getSize(), is(equalTo(0)));
    }

    @Test
    public void testConstructorParameter() {
        try {
            new LRUCacheImpl<>(4);
        } catch (final IllegalArgumentException e) {
            fail("Expected 4-element cache to be constructed normally");
        }

        try {
            new LRUCacheImpl<>(0);
            fail("Expected exception for zero-sized cache construction");
        } catch (final IllegalArgumentException e) {
            // Ok
        }

        try {
            new LRUCacheImpl<>(-4);
            fail("Expected exception for negative-sized cache construction");
        } catch (final IllegalArgumentException e) {
            // Ok
        }
    }

    @Test
    public void testAddingOneElementToEmptyCacheIncreasesSize() {
        final LRUCache<Object, Object> cache = new LRUCacheImpl<>(4);
        final int oldSize = cache.getSize();
        cache.put(0, TEST_OBJECTS.get(0));
        final int newSize = cache.getSize();
        assertThat("After adding one element to the empty cache, it is not more empty", cache.isEmpty(), is(equalTo(false)));
        assertThat("After adding one element to the empty cache, its size should increase", newSize, is(equalTo(1 + oldSize)));
    }

    @Test
    public void testAddingOneElementToFilledCacheDoesNotIncreaseSize() {
        final LRUCache<Object, Object> cache = new LRUCacheImpl<>(4);
        for (int i = 0; i < 4; i++) {
            cache.put(i, TEST_OBJECTS.get(i));
        }
        final int oldSize = cache.getSize();
        cache.put(4, TEST_OBJECTS.get(4));
        final int newSize = cache.getSize();
        assertThat("After filling up cache, its size does not increase", newSize, is(equalTo(oldSize)));
    }

    @Test
    public void testAddingOneElementToFilledCachePopsLastOneOut() {
        final LRUCache<Object, Object> cache = new LRUCacheImpl<>(4);
        for (int i = 0; i < 4; i++) {
            cache.put(i, TEST_OBJECTS.get(i));
        }
        final LRUCacheResponse<Object> response1 = cache.get(0);
        assertThat("After adding 4 elements to the 4-element cache, first one is still accessible", response1.isPresent(), is(equalTo(true)));
        assertThat("After adding 4 elements to the 4-element cache, first one hasn't changed", response1.value(), is(equalTo(TEST_OBJECTS.get(0))));
        cache.put(4, TEST_OBJECTS.get(4));
        final LRUCacheResponse<Object> response2 = cache.get(1);
        assertThat("After adding 5 elements to the 4-element cache, first one is no longer accessible", response2.isPresent(), is(equalTo(false)));
        final LRUCacheResponse<Object> response3 = cache.get(4);
        assertThat("After adding 5 elements to the 4-element cache, the most recently added element is still accessible", response3.isPresent(), is(equalTo(true)));
        assertThat("After adding 5 elements to the 4-element cache, the most recently added element hasn't changed", response3.value(), is(equalTo(TEST_OBJECTS.get(4))));
    }
}
