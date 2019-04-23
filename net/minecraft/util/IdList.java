/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.IndexedIterable;
import org.jetbrains.annotations.Nullable;

public class IdList<T>
implements IndexedIterable<T> {
    private int nextId;
    private final IdentityHashMap<T, Integer> idMap;
    private final List<T> list;

    public IdList() {
        this(512);
    }

    public IdList(int i) {
        this.list = Lists.newArrayListWithExpectedSize(i);
        this.idMap = new IdentityHashMap(i);
    }

    public void set(T object, int i) {
        this.idMap.put(object, i);
        while (this.list.size() <= i) {
            this.list.add(null);
        }
        this.list.set(i, object);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
    }

    public void add(T object) {
        this.set(object, this.nextId);
    }

    public int getId(T object) {
        Integer integer = this.idMap.get(object);
        return integer == null ? -1 : integer;
    }

    @Override
    @Nullable
    public final T get(int i) {
        if (i >= 0 && i < this.list.size()) {
            return this.list.get(i);
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.list.iterator(), Predicates.notNull());
    }

    public int size() {
        return this.idMap.size();
    }
}

