/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.shape.PairList;

public final class SimplePairList
implements PairList {
    private final DoubleArrayList valueIndices;
    private final IntArrayList minValues;
    private final IntArrayList maxValues;

    protected SimplePairList(DoubleList first, DoubleList second, boolean includeFirstOnly, boolean includeSecondOnly) {
        int i = 0;
        int j = 0;
        double d = Double.NaN;
        int k = first.size();
        int l = second.size();
        int m = k + l;
        this.valueIndices = new DoubleArrayList(m);
        this.minValues = new IntArrayList(m);
        this.maxValues = new IntArrayList(m);
        while (true) {
            double e;
            boolean bl2;
            boolean bl = i < k;
            boolean bl3 = bl2 = j < l;
            if (!bl && !bl2) break;
            boolean bl32 = bl && (!bl2 || first.getDouble(i) < second.getDouble(j) + 1.0E-7);
            double d2 = e = bl32 ? first.getDouble(i++) : second.getDouble(j++);
            if ((i == 0 || !bl) && !bl32 && !includeSecondOnly || (j == 0 || !bl2) && bl32 && !includeFirstOnly) continue;
            if (!(d >= e - 1.0E-7)) {
                this.minValues.add(i - 1);
                this.maxValues.add(j - 1);
                this.valueIndices.add(e);
                d = e;
                continue;
            }
            if (this.valueIndices.isEmpty()) continue;
            this.minValues.set(this.minValues.size() - 1, i - 1);
            this.maxValues.set(this.maxValues.size() - 1, j - 1);
        }
        if (this.valueIndices.isEmpty()) {
            this.valueIndices.add(Math.min(first.getDouble(k - 1), second.getDouble(l - 1)));
        }
    }

    @Override
    public boolean forEachPair(PairList.Consumer predicate) {
        for (int i = 0; i < this.valueIndices.size() - 1; ++i) {
            if (predicate.merge(this.minValues.getInt(i), this.maxValues.getInt(i), i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getPairs() {
        return this.valueIndices;
    }
}

