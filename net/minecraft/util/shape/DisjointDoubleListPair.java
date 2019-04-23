/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.DoubleListPair;

public class DisjointDoubleListPair
extends AbstractDoubleList
implements DoubleListPair {
    private final DoubleList first;
    private final DoubleList second;
    private final boolean field_1380;

    public DisjointDoubleListPair(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
        this.first = doubleList;
        this.second = doubleList2;
        this.field_1380 = bl;
    }

    @Override
    public int size() {
        return this.first.size() + this.second.size();
    }

    @Override
    public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
        if (this.field_1380) {
            return this.method_1067((i, j, k) -> sectionPairPredicate.merge(j, i, k));
        }
        return this.method_1067(sectionPairPredicate);
    }

    private boolean method_1067(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
        int j;
        int i = this.first.size() - 1;
        for (j = 0; j < i; ++j) {
            if (sectionPairPredicate.merge(j, -1, j)) continue;
            return false;
        }
        if (!sectionPairPredicate.merge(i, -1, i)) {
            return false;
        }
        for (j = 0; j < this.second.size(); ++j) {
            if (sectionPairPredicate.merge(i, j, i + 1 + j)) continue;
            return false;
        }
        return true;
    }

    @Override
    public double getDouble(int i) {
        if (i < this.first.size()) {
            return this.first.getDouble(i);
        }
        return this.second.getDouble(i - this.first.size());
    }

    @Override
    public DoubleList getMergedList() {
        return this;
    }
}

