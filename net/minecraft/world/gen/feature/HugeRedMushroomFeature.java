/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public class HugeRedMushroomFeature
extends HugeMushroomFeature {
    public HugeRedMushroomFeature(Codec<HugeMushroomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected void generateCap(WorldAccess world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
        for (int i = y - 3; i <= y; ++i) {
            int j = i < y ? config.foliageRadius : config.foliageRadius - 1;
            int k = config.foliageRadius - 2;
            for (int l = -j; l <= j; ++l) {
                for (int m = -j; m <= j; ++m) {
                    boolean bl6;
                    boolean bl = l == -j;
                    boolean bl2 = l == j;
                    boolean bl3 = m == -j;
                    boolean bl4 = m == j;
                    boolean bl5 = bl || bl2;
                    boolean bl7 = bl6 = bl3 || bl4;
                    if (i < y && bl5 == bl6) continue;
                    mutable.set(start, l, i, m);
                    if (world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) continue;
                    this.setBlockState(world, mutable, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)config.capProvider.getBlockState(random, start).with(MushroomBlock.UP, i >= y - 1)).with(MushroomBlock.WEST, l < -k)).with(MushroomBlock.EAST, l > k)).with(MushroomBlock.NORTH, m < -k)).with(MushroomBlock.SOUTH, m > k));
                }
            }
        }
    }

    @Override
    protected int getCapSize(int i, int j, int capSize, int y) {
        int k = 0;
        if (y < j && y >= j - 3) {
            k = capSize;
        } else if (y == j) {
            k = capSize;
        }
        return k;
    }
}

