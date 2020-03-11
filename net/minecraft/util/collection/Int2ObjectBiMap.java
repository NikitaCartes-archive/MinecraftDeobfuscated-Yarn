/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class Int2ObjectBiMap<K>
implements IndexedIterable<K> {
    private static final Object empty = null;
    private K[] values;
    private int[] ids;
    private K[] idToValues;
    private int nextId;
    private int size;

    public Int2ObjectBiMap(int size) {
        size = (int)((float)size / 0.8f);
        this.values = new Object[size];
        this.ids = new int[size];
        this.idToValues = new Object[size];
    }

    public int getId(@Nullable K value) {
        return this.getIdFromIndex(this.findIndex(value, this.getIdealIndex(value)));
    }

    @Override
    @Nullable
    public K get(int index) {
        if (index < 0 || index >= this.idToValues.length) {
            return null;
        }
        return this.idToValues[index];
    }

    private int getIdFromIndex(int index) {
        if (index == -1) {
            return -1;
        }
        return this.ids[index];
    }

    public int add(K value) {
        int i = this.nextId();
        this.put(value, i);
        return i;
    }

    private int nextId() {
        while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }

    private void resize(int newSize) {
        K[] objects = this.values;
        int[] is = this.ids;
        this.values = new Object[newSize];
        this.ids = new int[newSize];
        this.idToValues = new Object[newSize];
        this.nextId = 0;
        this.size = 0;
        for (int i = 0; i < objects.length; ++i) {
            if (objects[i] == null) continue;
            this.put(objects[i], is[i]);
        }
    }

    public void put(K value, int id) {
        int j;
        int i = Math.max(id, this.size + 1);
        if ((float)i >= (float)this.values.length * 0.8f) {
            for (j = this.values.length << 1; j < id; j <<= 1) {
            }
            this.resize(j);
        }
        j = this.findFree(this.getIdealIndex(value));
        this.values[j] = value;
        this.ids[j] = id;
        this.idToValues[id] = value;
        ++this.size;
        if (id == this.nextId) {
            ++this.nextId;
        }
    }

    private int getIdealIndex(@Nullable K value) {
        return (MathHelper.idealHash(System.identityHashCode(value)) & Integer.MAX_VALUE) % this.values.length;
    }

    private int findIndex(@Nullable K value, int id) {
        int i;
        for (i = id; i < this.values.length; ++i) {
            if (this.values[i] == value) {
                return i;
            }
            if (this.values[i] != empty) continue;
            return -1;
        }
        for (i = 0; i < id; ++i) {
            if (this.values[i] == value) {
                return i;
            }
            if (this.values[i] != empty) continue;
            return -1;
        }
        return -1;
    }

    private int findFree(int size) {
        int i;
        for (i = size; i < this.values.length; ++i) {
            if (this.values[i] != empty) continue;
            return i;
        }
        for (i = 0; i < size; ++i) {
            if (this.values[i] != empty) continue;
            return i;
        }
        throw new RuntimeException("Overflowed :(");
    }

    @Override
    public Iterator<K> iterator() {
        return Iterators.filter(Iterators.forArray(this.idToValues), Predicates.notNull());
    }

    public void clear() {
        Arrays.fill(this.values, null);
        Arrays.fill(this.idToValues, null);
        this.nextId = 0;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }
}

