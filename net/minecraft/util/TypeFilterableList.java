/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeFilterableList<T>
extends AbstractCollection<T> {
    private final Map<Class<?>, List<T>> elementsByType = Maps.newHashMap();
    private final Class<T> elementType;
    private final List<T> allElements = Lists.newArrayList();

    public TypeFilterableList(Class<T> class_) {
        this.elementType = class_;
        this.elementsByType.put(class_, this.allElements);
    }

    @Override
    public boolean add(T object) {
        boolean bl = false;
        for (Map.Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
            if (!entry.getKey().isInstance(object)) continue;
            bl |= entry.getValue().add(object);
        }
        return bl;
    }

    @Override
    public boolean remove(Object object) {
        boolean bl = false;
        for (Map.Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
            if (!entry.getKey().isInstance(object)) continue;
            List<T> list = entry.getValue();
            bl |= list.remove(object);
        }
        return bl;
    }

    @Override
    public boolean contains(Object object) {
        return this.getAllOfType(object.getClass()).contains(object);
    }

    public <S> Collection<S> getAllOfType(Class<S> class_2) {
        if (!this.elementType.isAssignableFrom(class_2)) {
            throw new IllegalArgumentException("Don't know how to search for " + class_2);
        }
        List list = this.elementsByType.computeIfAbsent(class_2, class_ -> this.allElements.stream().filter(class_::isInstance).collect(Collectors.toList()));
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public Iterator<T> iterator() {
        if (this.allElements.isEmpty()) {
            return Collections.emptyIterator();
        }
        return Iterators.unmodifiableIterator(this.allElements.iterator());
    }

    @Override
    public int size() {
        return this.allElements.size();
    }
}

