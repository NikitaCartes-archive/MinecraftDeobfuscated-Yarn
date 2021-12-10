/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

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
    @Nullable
    private final E initialElement;

    public static <E> DefaultedList<E> of() {
        return new DefaultedList<Object>(Lists.newArrayList(), null);
    }

    public static <E> DefaultedList<E> ofSize(int size) {
        return new DefaultedList<Object>(Lists.newArrayListWithCapacity(size), null);
    }

    public static <E> DefaultedList<E> ofSize(int size, E defaultValue) {
        Validate.notNull(defaultValue);
        Object[] objects = new Object[size];
        Arrays.fill(objects, defaultValue);
        return new DefaultedList<Object>(Arrays.asList(objects), defaultValue);
    }

    @SafeVarargs
    public static <E> DefaultedList<E> copyOf(E defaultValue, E ... values) {
        return new DefaultedList<E>(Arrays.asList(values), defaultValue);
    }

    protected DefaultedList(List<E> delegate, @Nullable E initialElement) {
        this.delegate = delegate;
        this.initialElement = initialElement;
    }

    @Override
    @NotNull
    public E get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public E set(int index, E element) {
        Validate.notNull(element);
        return this.delegate.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        Validate.notNull(element);
        this.delegate.add(index, element);
    }

    @Override
    public E remove(int index) {
        return this.delegate.remove(index);
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

