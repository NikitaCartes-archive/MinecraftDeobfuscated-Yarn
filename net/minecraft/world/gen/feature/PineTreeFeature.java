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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class PineTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

    public PineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function, false);
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
        int p;
        int o;
        int n;
        int m;
        int i = random.nextInt(5) + 7;
        int j = i - random.nextInt(2) - 3;
        int k = i - j;
        int l = 1 + random.nextInt(k + 1);
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        boolean bl = true;
        for (m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; ++m) {
            n = 1;
            n = m - blockPos.getY() < j ? 0 : l;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; ++o) {
                for (p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; ++p) {
                    if (m >= 0 && m < 256) {
                        if (PineTreeFeature.canTreeReplace(modifiableTestableWorld, mutable.set(o, m, p))) continue;
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
        if (!PineTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.down());
        m = 0;
        for (n = blockPos.getY() + i; n >= blockPos.getY() + j; --n) {
            for (int q = blockPos.getX() - m; q <= blockPos.getX() + m; ++q) {
                o = q - blockPos.getX();
                for (p = blockPos.getZ() - m; p <= blockPos.getZ() + m; ++p) {
                    BlockPos blockPos2;
                    int r = p - blockPos.getZ();
                    if (Math.abs(o) == m && Math.abs(r) == m && m > 0 || !PineTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(q, n, p))) continue;
                    this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
                }
            }
            if (m >= 1 && n == blockPos.getY() + j + 1) {
                --m;
                continue;
            }
            if (m >= l) continue;
            ++m;
        }
        for (n = 0; n < i - 1; ++n) {
            if (!PineTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos.up(n))) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos.up(n), LOG, mutableIntBoundingBox);
        }
        return true;
    }
}

