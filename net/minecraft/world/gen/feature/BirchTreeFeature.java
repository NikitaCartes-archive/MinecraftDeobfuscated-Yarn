/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class BirchTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.BIRCH_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
    private final boolean alwaysTall;

    public BirchTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, boolean bl2) {
        super(function, bl);
        this.alwaysTall = bl2;
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BlockBox blockBox) {
        int m;
        int l;
        int k;
        int j;
        int i = random.nextInt(3) + 5;
        if (this.alwaysTall) {
            i += random.nextInt(7);
        }
        boolean bl = true;
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        for (j = blockPos.getY(); j <= blockPos.getY() + 1 + i; ++j) {
            k = 1;
            if (j == blockPos.getY()) {
                k = 0;
            }
            if (j >= blockPos.getY() + 1 + i - 2) {
                k = 2;
            }
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; ++l) {
                for (m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; ++m) {
                    if (j >= 0 && j < 256) {
                        if (BirchTreeFeature.canTreeReplace(modifiableTestableWorld, mutable.set(l, j, m))) continue;
                        bl = false;
                        continue;
                    }
                    bl = false;
                }
            }
        }
        if (!bl) {
            return false;
        }
        if (!BirchTreeFeature.isDirtOrGrass(modifiableTestableWorld, blockPos.method_10074()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.method_10074());
        for (j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; ++j) {
            k = j - (blockPos.getY() + i);
            int n = 1 - k / 2;
            for (l = blockPos.getX() - n; l <= blockPos.getX() + n; ++l) {
                m = l - blockPos.getX();
                for (int o = blockPos.getZ() - n; o <= blockPos.getZ() + n; ++o) {
                    BlockPos blockPos2;
                    int p = o - blockPos.getZ();
                    if (Math.abs(m) == n && Math.abs(p) == n && (random.nextInt(2) == 0 || k == 0) || !BirchTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(l, j, o))) continue;
                    this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, blockBox);
                }
            }
        }
        for (j = 0; j < i; ++j) {
            if (!BirchTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos.up(j))) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos.up(j), LOG, blockBox);
        }
        return true;
    }
}

