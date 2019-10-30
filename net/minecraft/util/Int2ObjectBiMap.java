/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.util.IndexedIterable;
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

    public Int2ObjectBiMap(int i) {
        i = (int)((float)i / 0.8f);
        this.values = new Object[i];
        this.ids = new int[i];
        this.idToValues = new Object[i];
    }

    public int getId(@Nullable K object) {
        return this.getIdFromIndex(this.findIndex(object, this.getIdealIndex(object)));
    }

    @Override
    @Nullable
    public K get(int i) {
        if (i < 0 || i >= this.idToValues.length) {
            return null;
        }
        return this.idToValues[i];
    }

    private int getIdFromIndex(int i) {
        if (i == -1) {
            return -1;
        }
        return this.ids[i];
    }

    public int add(K object) {
        int i = this.nextId();
        this.put(object, i);
        return i;
    }

    private int nextId() {
        while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }

    private void resize(int i) {
        K[] objects = this.values;
        int[] is = this.ids;
        this.values = new Object[i];
        this.ids = new int[i];
        this.idToValues = new Object[i];
        this.nextId = 0;
        this.size = 0;
        for (int j = 0; j < objects.length; ++j) {
            if (objects[j] == null) continue;
            this.put(objects[j], is[j]);
        }
    }

    public void put(K object, int i) {
        int k;
        int j = Math.max(i, this.size + 1);
        if ((float)j >= (float)this.values.length * 0.8f) {
            for (k = this.values.length << 1; k < i; k <<= 1) {
            }
            this.resize(k);
        }
        k = this.findFree(this.getIdealIndex(object));
        this.values[k] = object;
        this.ids[k] = i;
        this.idToValues[i] = object;
        ++this.size;
        if (i == this.nextId) {
            ++this.nextId;
        }
    }

    private int getIdealIndex(@Nullable K object) {
        return (MathHelper.idealHash(System.identityHashCode(object)) & Integer.MAX_VALUE) % this.values.length;
    }

    private int findIndex(@Nullable K object, int i) {
        int j;
        for (j = i; j < this.values.length; ++j) {
            if (this.values[j] == object) {
                return j;
            }
            if (this.values[j] != empty) continue;
            return -1;
        }
        for (j = 0; j < i; ++j) {
            if (this.values[j] == object) {
                return j;
            }
            if (this.values[j] != empty) continue;
            return -1;
        }
        return -1;
    }

    private int findFree(int i) {
        int j;
        for (j = i; j < this.values.length; ++j) {
            if (this.values[j] != empty) continue;
            return j;
        }
        for (j = 0; j < i; ++j) {
            if (this.values[j] != empty) continue;
            return j;
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

