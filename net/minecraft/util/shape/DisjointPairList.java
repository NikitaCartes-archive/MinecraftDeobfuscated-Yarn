/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.PairList;

public class DisjointPairList
extends AbstractDoubleList
implements PairList {
    private final DoubleList first;
    private final DoubleList second;
    private final boolean inverted;

    public DisjointPairList(DoubleList first, DoubleList second, boolean inverted) {
        this.first = first;
        this.second = second;
        this.inverted = inverted;
    }

    @Override
    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean forEachPair(PairList.Consumer predicate) {
        if (this.inverted) {
            return this.iterateSections((i, j, k) -> predicate.merge(j, i, k));
        }
        return this.iterateSections(predicate);
    }

    private boolean iterateSections(PairList.Consumer consumer) {
        int j;
        int i = this.first.size() - 1;
        for (j = 0; j < i; ++j) {
            if (consumer.merge(j, -1, j)) continue;
            return false;
        }
        if (!consumer.merge(i, -1, i)) {
            return false;
        }
        for (j = 0; j < this.second.size(); ++j) {
            if (consumer.merge(i, j, i + 1 + j)) continue;
            return false;
        }
        return true;
    }

    @Override
    public double getDouble(int position) {
        if (position < this.first.size()) {
            return this.first.getDouble(position);
        }
        return this.second.getDouble(position - this.first.size());
    }

    @Override
    public DoubleList getPairs() {
        return this;
    }
}

