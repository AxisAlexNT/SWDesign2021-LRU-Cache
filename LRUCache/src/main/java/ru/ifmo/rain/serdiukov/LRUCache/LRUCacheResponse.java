package ru.ifmo.rain.serdiukov.LRUCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A record that's returned by cache as a response to the {@link LRUCache#get(Object)} method.
 * Provides a way to distinguish between {@code null} value presence and no value presence in one method call.
 * Unlike {@link java.util.Optional}, this record can distinguish between storing {@code null} values and no value presence.
 * The value of {@code value} field is not defined in case {@code isPresent == false}.
 * @param <V> Type of value that's stored in this cache.
 */
public record LRUCacheResponse<V>(@Nullable V value, boolean isPresent) {
}
