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
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class SavannaTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.ACACIA_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();

    public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
        super(function, bl);
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BlockBox blockBox) {
        int q;
        int m;
        int l;
        int k;
        int i = random.nextInt(3) + random.nextInt(3) + 5;
        boolean bl = true;
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; ++j) {
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
                        if (SavannaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable.set(l, j, m))) continue;
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
        if (!SavannaTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.method_10074()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.method_10074());
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        k = i - random.nextInt(4) - 1;
        int n = 3 - random.nextInt(3);
        l = blockPos.getX();
        m = blockPos.getZ();
        int o = 0;
        for (int p = 0; p < i; ++p) {
            BlockPos blockPos2;
            q = blockPos.getY() + p;
            if (p >= k && n > 0) {
                l += direction.getOffsetX();
                m += direction.getOffsetZ();
                --n;
            }
            if (!SavannaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(l, q, m))) continue;
            this.addLog(set, modifiableTestableWorld, blockPos2, blockBox);
            o = q;
        }
        BlockPos blockPos3 = new BlockPos(l, o, m);
        for (q = -3; q <= 3; ++q) {
            for (int r = -3; r <= 3; ++r) {
                if (Math.abs(q) == 3 && Math.abs(r) == 3) continue;
                this.addLeaves(set, modifiableTestableWorld, blockPos3.add(q, 0, r), blockBox);
            }
        }
        blockPos3 = blockPos3.up();
        for (q = -1; q <= 1; ++q) {
            for (int r = -1; r <= 1; ++r) {
                this.addLeaves(set, modifiableTestableWorld, blockPos3.add(q, 0, r), blockBox);
            }
        }
        this.addLeaves(set, modifiableTestableWorld, blockPos3.east(2), blockBox);
        this.addLeaves(set, modifiableTestableWorld, blockPos3.west(2), blockBox);
        this.addLeaves(set, modifiableTestableWorld, blockPos3.south(2), blockBox);
        this.addLeaves(set, modifiableTestableWorld, blockPos3.north(2), blockBox);
        l = blockPos.getX();
        m = blockPos.getZ();
        Direction direction2 = Direction.Type.HORIZONTAL.random(random);
        if (direction2 != direction) {
            int t;
            q = k - random.nextInt(2) - 1;
            int r = 1 + random.nextInt(3);
            o = 0;
            for (int s = q; s < i && r > 0; ++s, --r) {
                if (s < 1) continue;
                t = blockPos.getY() + s;
                BlockPos blockPos4 = new BlockPos(l += direction2.getOffsetX(), t, m += direction2.getOffsetZ());
                if (!SavannaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos4)) continue;
                this.addLog(set, modifiableTestableWorld, blockPos4, blockBox);
                o = t;
            }
            if (o > 0) {
                BlockPos blockPos5 = new BlockPos(l, o, m);
                for (t = -2; t <= 2; ++t) {
                    for (int u = -2; u <= 2; ++u) {
                        if (Math.abs(t) == 2 && Math.abs(u) == 2) continue;
                        this.addLeaves(set, modifiableTestableWorld, blockPos5.add(t, 0, u), blockBox);
                    }
                }
                blockPos5 = blockPos5.up();
                for (t = -1; t <= 1; ++t) {
                    for (int u = -1; u <= 1; ++u) {
                        this.addLeaves(set, modifiableTestableWorld, blockPos5.add(t, 0, u), blockBox);
                    }
                }
            }
        }
        return true;
    }

    private void addLog(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockBox blockBox) {
        this.setBlockState(set, modifiableWorld, blockPos, LOG, blockBox);
    }

    private void addLeaves(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BlockBox blockBox) {
        if (SavannaTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos)) {
            this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
        }
    }
}

