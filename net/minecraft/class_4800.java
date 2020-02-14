/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public class class_4800 {
    public static Optional<BlockPos> method_24501(BlockPos blockPos, int i, int j, Predicate<BlockPos> predicate) {
        if (predicate.test(blockPos)) {
            return Optional.of(blockPos);
        }
        int k = Math.max(i, j);
        BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
        for (int l = 1; l <= k; ++l) {
            for (int m = -l; m <= l; ++m) {
                if (m > i || m < -i) continue;
                boolean bl = m == -l || m == l;
                for (int n = -l; n <= l; ++n) {
                    if (n > j || n < -j) continue;
                    boolean bl2 = n == -l || n == l;
                    for (int o = -l; o <= l; ++o) {
                        boolean bl3;
                        if (o > i || o < -i) continue;
                        boolean bl4 = bl3 = o == -l || o == l;
                        if (!bl && !bl2 && !bl3 || !predicate.test(mutable.set(blockPos).setOffset(m, n, o))) continue;
                        return Optional.of(blockPos.add(m, n, o));
                    }
                }
            }
        }
        return Optional.empty();
    }
}

