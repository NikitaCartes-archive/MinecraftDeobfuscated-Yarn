/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomBlock;
import net.minecraft.class_4625;
import net.minecraft.class_4635;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class HugeRedMushroomFeature
extends class_4625 {
    public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends class_4635> function) {
        super(function);
    }

    @Override
    protected void method_23375(IWorld iWorld, Random random, BlockPos blockPos, int i, BlockPos.Mutable mutable, class_4635 arg) {
        for (int j = i - 3; j <= i; ++j) {
            int k = j < i ? arg.field_21232 : arg.field_21232 - 1;
            int l = arg.field_21232 - 2;
            for (int m = -k; m <= k; ++m) {
                for (int n = -k; n <= k; ++n) {
                    boolean bl6;
                    boolean bl = m == -k;
                    boolean bl2 = m == k;
                    boolean bl3 = n == -k;
                    boolean bl4 = n == k;
                    boolean bl5 = bl || bl2;
                    boolean bl7 = bl6 = bl3 || bl4;
                    if (j < i && bl5 == bl6) continue;
                    mutable.set(blockPos).setOffset(m, j, n);
                    if (iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) continue;
                    this.setBlockState(iWorld, mutable, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)arg.field_21230.method_23455(random, blockPos).with(MushroomBlock.UP, j >= i - 1)).with(MushroomBlock.WEST, m < -l)).with(MushroomBlock.EAST, m > l)).with(MushroomBlock.NORTH, n < -l)).with(MushroomBlock.SOUTH, n > l));
                }
            }
        }
    }

    @Override
    protected int method_23372(int i, int j, int k, int l) {
        int m = 0;
        if (l < j && l >= j - 3) {
            m = k;
        } else if (l == j) {
            m = k;
        }
        return m;
    }
}

