/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.tag.Tag;

public class SetTag<T>
implements Tag<T> {
    private final ImmutableList<T> valueList;
    private final Set<T> valueSet;
    @VisibleForTesting
    protected final Class<?> type;

    protected SetTag(Set<T> values, Class<?> type) {
        this.type = type;
        this.valueSet = values;
        this.valueList = ImmutableList.copyOf(values);
    }

    public static <T> SetTag<T> empty() {
        return new SetTag(ImmutableSet.of(), Void.class);
    }

    public static <T> SetTag<T> of(Set<T> values) {
        return new SetTag<T>(values, SetTag.getCommonType(values));
    }

    @Override
    public boolean contains(T entry) {
        return this.type.isInstance(entry) && this.valueSet.contains(entry);
    }

    @Override
    public List<T> values() {
        return this.valueList;
    }

    private static <T> Class<?> getCommonType(Set<T> values) {
        if (values.isEmpty()) {
            return Void.class;
        }
        Class<?> class_ = null;
        for (T object : values) {
            if (class_ == null) {
                class_ = object.getClass();
                continue;
            }
            class_ = SetTag.getCommonType(class_, object.getClass());
        }
        return class_;
    }

    private static Class<?> getCommonType(Class<?> first, Class<?> second) {
        while (!first.isAssignableFrom(second)) {
            first = first.getSuperclass();
        }
        return first;
    }
}

