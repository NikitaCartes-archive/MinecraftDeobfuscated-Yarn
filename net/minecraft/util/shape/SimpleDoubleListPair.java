/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.shape.DoubleListPair;

public final class SimpleDoubleListPair
implements DoubleListPair {
    private final DoubleArrayList mergedList;
    private final IntArrayList field_1376;
    private final IntArrayList field_1378;

    protected SimpleDoubleListPair(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        int i = 0;
        int j = 0;
        double d = Double.NaN;
        int k = doubleList.size();
        int l = doubleList2.size();
        int m = k + l;
        this.mergedList = new DoubleArrayList(m);
        this.field_1376 = new IntArrayList(m);
        this.field_1378 = new IntArrayList(m);
        while (true) {
            double e;
            boolean bl4;
            boolean bl3 = i < k;
            boolean bl5 = bl4 = j < l;
            if (!bl3 && !bl4) break;
            boolean bl52 = bl3 && (!bl4 || doubleList.getDouble(i) < doubleList2.getDouble(j) + 1.0E-7);
            double d2 = e = bl52 ? doubleList.getDouble(i++) : doubleList2.getDouble(j++);
            if ((i == 0 || !bl3) && !bl52 && !bl2 || (j == 0 || !bl4) && bl52 && !bl) continue;
            if (!(d >= e - 1.0E-7)) {
                this.field_1376.add(i - 1);
                this.field_1378.add(j - 1);
                this.mergedList.add(e);
                d = e;
                continue;
            }
            if (this.mergedList.isEmpty()) continue;
            this.field_1376.set(this.field_1376.size() - 1, i - 1);
            this.field_1378.set(this.field_1378.size() - 1, j - 1);
        }
        if (this.mergedList.isEmpty()) {
            this.mergedList.add(Math.min(doubleList.getDouble(k - 1), doubleList2.getDouble(l - 1)));
        }
    }

    @Override
    public boolean forAllOverlappingSections(DoubleListPair.SectionPairPredicate sectionPairPredicate) {
        for (int i = 0; i < this.mergedList.size() - 1; ++i) {
            if (sectionPairPredicate.merge(this.field_1376.getInt(i), this.field_1378.getInt(i), i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getMergedList() {
        return this.mergedList;
    }
}

