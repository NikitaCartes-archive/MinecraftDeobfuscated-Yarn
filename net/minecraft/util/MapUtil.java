/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapUtil {
    public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values) {
        return MapUtil.createMap(keys, values, Maps.newLinkedHashMap());
    }

    public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values, Map<K, V> result) {
        Iterator<V> iterator = values.iterator();
        for (K object : keys) {
            result.put(object, iterator.next());
        }
        if (iterator.hasNext()) {
            throw new NoSuchElementException();
        }
        return result;
    }
}

