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

public class SpruceTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

    public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
        super(function, bl);
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BlockBox blockBox) {
        int p;
        int o;
        int n;
        int m;
        int i = random.nextInt(4) + 6;
        int j = 1 + random.nextInt(2);
        int k = i - j;
        int l = 2 + random.nextInt(2);
        boolean bl = true;
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        for (m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; ++m) {
            n = m - blockPos.getY() < j ? 0 : l;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; ++o) {
                for (p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; ++p) {
                    if (m >= 0 && m < 256) {
                        mutable.set(o, m, p);
                        if (SpruceTreeFeature.isAirOrLeaves(modifiableTestableWorld, mutable)) continue;
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
        if (!SpruceTreeFeature.isDirtOrGrass(modifiableTestableWorld, blockPos.down()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.down());
        m = random.nextInt(2);
        n = 1;
        int q = 0;
        for (o = 0; o <= k; ++o) {
            p = blockPos.getY() + i - o;
            for (int r = blockPos.getX() - m; r <= blockPos.getX() + m; ++r) {
                int s = r - blockPos.getX();
                for (int t = blockPos.getZ() - m; t <= blockPos.getZ() + m; ++t) {
                    BlockPos blockPos2;
                    int u = t - blockPos.getZ();
                    if (Math.abs(s) == m && Math.abs(u) == m && m > 0 || !SpruceTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(r, p, t)) && !SpruceTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos2)) continue;
                    this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, blockBox);
                }
            }
            if (m >= n) {
                m = q;
                q = 1;
                if (++n <= l) continue;
                n = l;
                continue;
            }
            ++m;
        }
        o = random.nextInt(3);
        for (p = 0; p < i - o; ++p) {
            if (!SpruceTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos.up(p))) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos.up(p), LOG, blockBox);
        }
        return true;
    }
}

