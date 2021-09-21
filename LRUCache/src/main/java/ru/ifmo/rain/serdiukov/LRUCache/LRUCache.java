package ru.ifmo.rain.serdiukov.LRUCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for the Cache with LRU replacement policy.
 *
 * @param <K> Type of keys stored in this cache.
 * @param <V> Type of values stored in this cache.
 */
public interface LRUCache<K, V> {
    /**
     * Puts new value into LRU cache, associated with key K. If given key has no mapping, associates given value with this key, otherwise overwrites previous value associated with given key.
     *
     * @param key   a key to be associated with {@code value}.
     * @param value a value that's going to be associated with {@code key}.
     */
    void put(final @NotNull K key, final @Nullable V value);

    /**
     * Fetches a value with given key from this cache. If there is a mapping between {@code key} and some {@code value}, returns the {@link LRUCacheResponse} with the last one that was associated and {@code isPresent == true}. Otherwise, returns {@link LRUCacheResponse} with {@code isPresent == false}.
     * This operation updates last-access statistics for the fetched element, if it's found.
     *
     * @param key a key to be searched for.
     * @return a {@link LRUCacheResponse} structure holding information on whether an element with given key was found or not.
     */
    LRUCacheResponse<V> get(final @NotNull K key);

    /**
     * Retrieves the number of elements this LRU Cache is constructed to store.
     *
     * @return Maximum number of elements that are to be stored in this LRU Cache.
     */
    int getCapacity();

    /**
     * Retrieves the number of elements that are stored in this LRU Cache right now.
     *
     * @return Number of elements that are stored in this LRU Cache right now.
     */
    int getSize();

    /**
     * Checks whether this LRU Cache is empty.
     *
     * @return {@code true} if and only of this LRU Cache is empty, {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Check whether this LRU Cache has reached its capacity so that adding new elements requires removing least recently used ones.
     *
     * @return {@code true} if and only if this LRU Cache has reached its capacity, {@code false} otherwise.
     */
    boolean isReachedCapacity();
}
