/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;

public class DarkOakTreeFeature
extends AbstractTreeFeature<MegaTreeFeatureConfig> {
    public DarkOakTreeFeature(Function<Dynamic<?>, ? extends MegaTreeFeatureConfig> function) {
        super(function);
    }

    public boolean method_23389(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        int s;
        int r;
        int i = random.nextInt(3) + random.nextInt(2) + megaTreeFeatureConfig.baseHeight;
        int j = blockPos.getX();
        int k = blockPos.getY();
        int l = blockPos.getZ();
        if (k < 1 || k + i + 1 >= 256) {
            return false;
        }
        BlockPos blockPos2 = blockPos.method_10074();
        if (!DarkOakTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
            return false;
        }
        if (!this.doesTreeFit(modifiableTestableWorld, blockPos, i)) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos2);
        this.setToDirt(modifiableTestableWorld, blockPos2.east());
        this.setToDirt(modifiableTestableWorld, blockPos2.south());
        this.setToDirt(modifiableTestableWorld, blockPos2.south().east());
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        int m = i - random.nextInt(4);
        int n = 2 - random.nextInt(3);
        int o = j;
        int p = l;
        int q = k + i - 1;
        for (r = 0; r < i; ++r) {
            BlockPos blockPos3;
            if (r >= m && n > 0) {
                o += direction.getOffsetX();
                p += direction.getOffsetZ();
                --n;
            }
            if (!DarkOakTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos3 = new BlockPos(o, s = k + r, p))) continue;
            this.method_23382(modifiableTestableWorld, random, blockPos3, set, blockBox, megaTreeFeatureConfig);
            this.method_23382(modifiableTestableWorld, random, blockPos3.east(), set, blockBox, megaTreeFeatureConfig);
            this.method_23382(modifiableTestableWorld, random, blockPos3.south(), set, blockBox, megaTreeFeatureConfig);
            this.method_23382(modifiableTestableWorld, random, blockPos3.east().south(), set, blockBox, megaTreeFeatureConfig);
        }
        for (r = -2; r <= 0; ++r) {
            for (s = -2; s <= 0; ++s) {
                int t = -1;
                this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
                if (r <= -2 && s <= -1 || r == -1 && s == -2) continue;
                t = 1;
                this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
                this.method_23383(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
            }
        }
        if (random.nextBoolean()) {
            this.method_23383(modifiableTestableWorld, random, new BlockPos(o, q + 2, p), set2, blockBox, megaTreeFeatureConfig);
            this.method_23383(modifiableTestableWorld, random, new BlockPos(o + 1, q + 2, p), set2, blockBox, megaTreeFeatureConfig);
            this.method_23383(modifiableTestableWorld, random, new BlockPos(o + 1, q + 2, p + 1), set2, blockBox, megaTreeFeatureConfig);
            this.method_23383(modifiableTestableWorld, random, new BlockPos(o, q + 2, p + 1), set2, blockBox, megaTreeFeatureConfig);
        }
        for (r = -3; r <= 4; ++r) {
            for (s = -3; s <= 4; ++s) {
                if (r == -3 && s == -3 || r == -3 && s == 4 || r == 4 && s == -3 || r == 4 && s == 4 || Math.abs(r) >= 3 && Math.abs(s) >= 3) continue;
                this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r, q, p + s), set2, blockBox, megaTreeFeatureConfig);
            }
        }
        for (r = -1; r <= 2; ++r) {
            for (s = -1; s <= 2; ++s) {
                int v;
                int u;
                if (r >= 0 && r <= 1 && s >= 0 && s <= 1 || random.nextInt(3) > 0) continue;
                int t = random.nextInt(3) + 2;
                for (u = 0; u < t; ++u) {
                    this.method_23382(modifiableTestableWorld, random, new BlockPos(j + r, q - u - 1, l + s), set, blockBox, megaTreeFeatureConfig);
                }
                for (u = -1; u <= 1; ++u) {
                    for (v = -1; v <= 1; ++v) {
                        this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r + u, q, p + s + v), set2, blockBox, megaTreeFeatureConfig);
                    }
                }
                for (u = -2; u <= 2; ++u) {
                    for (v = -2; v <= 2; ++v) {
                        if (Math.abs(u) == 2 && Math.abs(v) == 2) continue;
                        this.method_23383(modifiableTestableWorld, random, new BlockPos(o + r + u, q - 1, p + s + v), set2, blockBox, megaTreeFeatureConfig);
                    }
                }
            }
        }
        return true;
    }

    private boolean doesTreeFit(TestableWorld testableWorld, BlockPos blockPos, int i) {
        int j = blockPos.getX();
        int k = blockPos.getY();
        int l = blockPos.getZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int m = 0; m <= i + 1; ++m) {
            int n = 1;
            if (m == 0) {
                n = 0;
            }
            if (m >= i - 1) {
                n = 2;
            }
            for (int o = -n; o <= n; ++o) {
                for (int p = -n; p <= n; ++p) {
                    if (DarkOakTreeFeature.canTreeReplace(testableWorld, mutable.set(j + o, k + m, l + p))) continue;
                    return false;
                }
            }
        }
        return true;
    }
}

