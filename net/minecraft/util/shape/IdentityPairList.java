/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.PairList;

public class IdentityPairList
implements PairList {
    private final DoubleList merged;

    public IdentityPairList(DoubleList values) {
        this.merged = values;
    }

    @Override
    public boolean forEachPair(PairList.Consumer predicate) {
        for (int i = 0; i <= this.merged.size(); ++i) {
            if (predicate.merge(i, i, i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getPairs() {
        return this.merged;
    }
}

