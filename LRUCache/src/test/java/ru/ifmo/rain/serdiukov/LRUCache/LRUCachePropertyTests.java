package ru.ifmo.rain.serdiukov.LRUCache;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runner.RunWith;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@RunWith(JUnitQuickcheck.class)
public class LRUCachePropertyTests {

    @Property(trials = 50)
    public void sizeTest(final LinkedHashMap<Object, Object> objects) {
        final LRUCache<Object, Object> cache = new LRUCacheImpl<>(4);
        assertThat("empty cache should be empty", cache.isEmpty(), is(equalTo(true)));
        assertThat("empty cache should contain 0 elements", cache.getSize(), is(equalTo(0)));

        for (final Map.Entry<Object, Object> keyValuePair : objects.entrySet()) {
            cache.put(keyValuePair.getKey(), keyValuePair.getValue());
        }

        assertThat("Adding elements has changed size accordingly", cache.getSize(), equalTo(Integer.min(objects.size(), cache.getCapacity())));
    }

    @Property(trials = 500)
    public void lastAddedElementTest(final LinkedHashMap<@NotNull Object, @Nullable Object> objects, @InRange(minInt = 1, maxInt = 40) int capacity) {
        if (objects.isEmpty()) {
            return;
        }

        capacity = Integer.min(capacity, 1 + (objects.size()) / 2);
        final LRUCache<@NotNull Object, Object> cache = new LRUCacheImpl<>(capacity);
        assertThat("empty cache should be empty", cache.isEmpty(), is(equalTo(true)));
        assertThat("empty cache should contain 0 elements", cache.getSize(), is(equalTo(0)));


        for (final Map.Entry<@NotNull Object, @Nullable Object> keyValuePair : objects.entrySet()) {
            cache.put(keyValuePair.getKey(), keyValuePair.getValue());
        }

        final int testSize = objects.size();
        final List<Map.Entry<@NotNull Object, @Nullable Object>> linearObjects = new ArrayList<>(objects.entrySet());
        final LinkedHashSet<Map.Entry<@NotNull Object, @Nullable Object>> oldElements = new LinkedHashSet<>();
        final LinkedHashMap<@NotNull Object, @Nullable Object> recentElements = new LinkedHashMap<>();

        for (int i = testSize - 1; i >= 0; --i) {
            final Map.Entry<@NotNull Object, @Nullable Object> kV = linearObjects.get(i);
            if (recentElements.size() < capacity) {
                if (!recentElements.containsKey(kV.getKey())) {
                    recentElements.put(kV.getKey(), kV.getValue());
                } else {
                    // NOTE: Reference comparison is intended
                    if (recentElements.get(kV.getKey()) != (kV.getValue())) {
                        oldElements.add(kV);
                    }
                }
            } else {
                // NOTE: Reference comparison is intended
                if (recentElements.get(kV.getKey()) != (kV.getValue())) {
                    oldElements.add(kV);
                }
            }
        }

        // Check that recent values are present in this cache:
        for (final Map.Entry<@NotNull Object, @Nullable Object> KV : recentElements.entrySet()) {
            final LRUCacheResponse<Object> mustNotBeEmptyResponse = cache.get(KV.getKey());
            assertThat("Recent values must not be response", mustNotBeEmptyResponse.isPresent(), is(equalTo(true)));
            assertThat("Recent values must not be changed", mustNotBeEmptyResponse.value(), is(equalTo(KV.getValue())));
        }


        // Check that old values are not present in this cache:
        for (final Map.Entry<@NotNull Object, @Nullable Object> oldKV : oldElements) {
            final LRUCacheResponse<Object> mustBeAnotherResponse = cache.get(oldKV.getKey());
            if (mustBeAnotherResponse.isPresent()) {
                // NOTE: Reference comparison is intended
                if (mustBeAnotherResponse.value() == oldKV.getValue()) {
                    fail("LRU Cache must have overwritten old value");
                }
            }
        }

        assertThat("Adding elements has changed size accordingly", cache.getSize(), equalTo(Integer.min(objects.size(), cache.getCapacity())));
    }
}
