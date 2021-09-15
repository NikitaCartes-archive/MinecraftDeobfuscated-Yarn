/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class class_6496 {
    private class_6496() {
    }

    public static <T> boolean method_37951(Map<T, Set<T>> map, Set<T> set, Set<T> set2, Consumer<T> consumer, T object) {
        if (set.contains(object)) {
            return false;
        }
        if (set2.contains(object)) {
            return true;
        }
        set2.add(object);
        for (Object object2 : (Set)map.getOrDefault(object, ImmutableSet.of())) {
            if (!class_6496.method_37951(map, set, set2, consumer, object2)) continue;
            return true;
        }
        set2.remove(object);
        set.add(object);
        consumer.accept(object);
        return false;
    }
}

