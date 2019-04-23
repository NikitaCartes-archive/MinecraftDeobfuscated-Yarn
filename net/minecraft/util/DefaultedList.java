/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultedList<E>
extends AbstractList<E> {
    private final List<E> underlying;
    private final E defaultValue;

    public static <E> DefaultedList<E> create() {
        return new DefaultedList<E>();
    }

    public static <E> DefaultedList<E> create(int i, E object) {
        Validate.notNull(object);
        Object[] objects = new Object[i];
        Arrays.fill(objects, object);
        return new DefaultedList<Object>(Arrays.asList(objects), object);
    }

    @SafeVarargs
    public static <E> DefaultedList<E> create(E object, E ... objects) {
        return new DefaultedList<E>(Arrays.asList(objects), object);
    }

    protected DefaultedList() {
        this(new ArrayList(), null);
    }

    protected DefaultedList(List<E> list, @Nullable E object) {
        this.underlying = list;
        this.defaultValue = object;
    }

    @Override
    @NotNull
    public E get(int i) {
        return this.underlying.get(i);
    }

    @Override
    public E set(int i, E object) {
        Validate.notNull(object);
        return this.underlying.set(i, object);
    }

    @Override
    public void add(int i, E object) {
        Validate.notNull(object);
        this.underlying.add(i, object);
    }

    @Override
    public E remove(int i) {
        return this.underlying.remove(i);
    }

    @Override
    public int size() {
        return this.underlying.size();
    }

    @Override
    public void clear() {
        if (this.defaultValue == null) {
            super.clear();
        } else {
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, this.defaultValue);
            }
        }
    }
}

