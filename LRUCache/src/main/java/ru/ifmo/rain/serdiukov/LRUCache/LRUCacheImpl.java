package ru.ifmo.rain.serdiukov.LRUCache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU Cache implementation.
 * @param <K> Type of Keys for this cache.
 * @param <V> Type of values for this cache.
 */
public class LRUCacheImpl<K, V> implements LRUCache<K, V> {
    private final Map<K, ChainNode<K, V>> keyToChainNodes = new HashMap<>();
    private final int capacity;
    private ChainNode<K, V> head, tail;
    private int size;


    /**
     * Constructs a new LRU Cache with the given capacity.
     *
     * @param capacity Number of elements that should be stored prior to be overwritten. Must be a positive integer ({@code capacity > 0}).
     * @throws IllegalArgumentException in case given {@code capacity} is not a positive integer.
     */
    public LRUCacheImpl(final int capacity) throws IllegalArgumentException {
        if (!(capacity > 0)) {
            throw new IllegalArgumentException("LRU Cache capacity must be a positive integer");
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(final @NotNull K key, final @Nullable V value) {
        if (size == capacity) {
            popLastElement();
        } else if (size > capacity) {
            assert false : "LRU Cache has size greater than its capacity";
            throw new IllegalStateException("LRU Cache has size greater than its capacity");
        }
        assert (size < capacity) : "After checking capacity constraint, size is still not less than capacity";

        final ChainNode<K, V> newHead = new ChainNode<>(key, value);
        final ChainNode<K, V> oldHead = this.head;

        if (oldHead != null) {
            assert (this.tail != null) : "Null tail while putting an element to the non-empty LRU Cache";
            newHead.next = oldHead;
            oldHead.previous = newHead;
        } else {
            assert (this.tail == null) : "Non-null tail while putting an element to the empty LRU Cache";
            this.tail = newHead;
        }
        ++size;
        this.head = newHead;
        keyToChainNodes.put(key, newHead);
    }

    private synchronized void popLastElement() {
        if (this.tail == null) {
            assert (this.head == null) : "LRU Cache has a null tail, but non-null head";
            return;
        }
        final K leastRecentlyUsedKey = this.tail.key;
        this.keyToChainNodes.remove(leastRecentlyUsedKey);
        if (this.tail.previous == null) {
            assert (this.head == this.tail) : "Current tail does not have any previous element, but is not equal to the LRU Cache's head";
            this.head = null;
            this.tail = null;
        } else {
            this.tail.previous.next = null;
            this.tail = this.tail.previous;
        }
        --size;
    }

    @Override
    public synchronized LRUCacheResponse<V> get(final @NotNull K key) {
        if (!this.keyToChainNodes.containsKey(key)) {
            return new LRUCacheResponse<>(null, false);
        }
        final ChainNode<K, V> elementNode = this.keyToChainNodes.get(key);
        // Put this element into the head
        if (elementNode.previous == null) {
            assert (elementNode == this.head) : "elementNode does not have previous element, but is not at the head of this LRU Cache";
            // Nothing to be done in this case, node is already at the top
        } else {
            assert (elementNode != this.head) : "elementNode has a previous element, but is at the head of this LRU Cache";
            if (elementNode.next == null) {
                assert (elementNode == this.tail) : "elementNode does not have next element, but is not at the tail of this LRU Cache";
                elementNode.previous.next = null;
                this.tail = elementNode.previous;
            } else {
                assert (elementNode != this.tail) : "elementNode has a next element, but is at the tail of this LRU Cache";
                elementNode.next.previous = elementNode.previous;
                elementNode.previous.next = elementNode.next;
            }
            elementNode.previous = null;
            elementNode.next = this.head;
            this.head.previous = elementNode;
            this.head = elementNode;
        }
        return new LRUCacheResponse<>(elementNode.value, true);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (this.size == 0) {
            assert (this.head == null && this.tail == null) : "LRU Cache size counter is zero, but head and tail are not null";
            return true;
        } else {
            assert (this.head != null && this.tail != null) : "LRU Cache size counter is zero, but head and tail are not both non-null";
            return false;
        }
    }

    @Override
    public boolean isReachedCapacity() {
        return this.capacity == this.size;
    }

    @RequiredArgsConstructor
    @Getter
    private static class ChainNode<K, V> {
        private final @NotNull K key;
        private final @Nullable V value;
        @Nullable ChainNode<K, V> previous, next;
    }
}
