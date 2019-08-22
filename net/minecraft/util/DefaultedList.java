/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultedList<E>
extends AbstractList<E> {
    private final List<E> delegate;
    private final E initialElement;

    public static <E> DefaultedList<E> of() {
        return new DefaultedList<E>();
    }

    public static <E> DefaultedList<E> ofSize(int i, E object) {
        Validate.notNull(object);
        Object[] objects = new Object[i];
        Arrays.fill(objects, object);
        return new DefaultedList<Object>(Arrays.asList(objects), object);
    }

    @SafeVarargs
    public static <E> DefaultedList<E> copyOf(E object, E ... objects) {
        return new DefaultedList<E>(Arrays.asList(objects), object);
    }

    protected DefaultedList() {
        this(Lists.newArrayList(), null);
    }

    protected DefaultedList(List<E> list, @Nullable E object) {
        this.delegate = list;
        this.initialElement = object;
    }

    @Override
    @NotNull
    public E get(int i) {
        return this.delegate.get(i);
    }

    @Override
    public E set(int i, E object) {
        Validate.notNull(object);
        return this.delegate.set(i, object);
    }

    @Override
    public void add(int i, E object) {
        Validate.notNull(object);
        this.delegate.add(i, object);
    }

    @Override
    public E remove(int i) {
        return this.delegate.remove(i);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        if (this.initialElement == null) {
            super.clear();
        } else {
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, this.initialElement);
            }
        }
    }
}

