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
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.AbstractTreeFeatureConfig;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;

public abstract class MegaTreeFeature<T extends AbstractTreeFeatureConfig>
extends AbstractTreeFeature<T> {
    public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected int getHeight(Random random, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        int i = random.nextInt(3) + megaTreeFeatureConfig.baseHeight;
        if (megaTreeFeatureConfig.field_21233 > 1) {
            i += random.nextInt(megaTreeFeatureConfig.field_21233);
        }
        return i;
    }

    private boolean doesTreeFit(TestableWorld testableWorld, BlockPos blockPos, int i) {
        boolean bl = true;
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        for (int j = 0; j <= 1 + i; ++j) {
            int k = 2;
            if (j == 0) {
                k = 1;
            } else if (j >= 1 + i - 2) {
                k = 2;
            }
            for (int l = -k; l <= k && bl; ++l) {
                for (int m = -k; m <= k && bl; ++m) {
                    if (blockPos.getY() + j >= 0 && blockPos.getY() + j < 256 && MegaTreeFeature.canTreeReplace(testableWorld, blockPos.add(l, j, m))) continue;
                    bl = false;
                }
            }
        }
        return bl;
    }

    private boolean replaceGround(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.method_10074();
        if (!MegaTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2) || blockPos.getY() < 2) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos2);
        this.setToDirt(modifiableTestableWorld, blockPos2.east());
        this.setToDirt(modifiableTestableWorld, blockPos2.south());
        this.setToDirt(modifiableTestableWorld, blockPos2.south().east());
        return true;
    }

    protected boolean checkTreeFitsAndReplaceGround(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i) {
        return this.doesTreeFit(modifiableTestableWorld, blockPos, i) && this.replaceGround(modifiableTestableWorld, blockPos);
    }

    protected void makeSquaredLeafLayer(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, AbstractTreeFeatureConfig abstractTreeFeatureConfig) {
        int j = i * i;
        for (int k = -i; k <= i + 1; ++k) {
            for (int l = -i; l <= i + 1; ++l) {
                int n;
                int m = Math.min(Math.abs(k), Math.abs(k - 1));
                if (m + (n = Math.min(Math.abs(l), Math.abs(l - 1))) >= 7 || m * m + n * n > j) continue;
                this.method_23383(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, abstractTreeFeatureConfig);
            }
        }
    }

    protected void makeRoundLeafLayer(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, AbstractTreeFeatureConfig abstractTreeFeatureConfig) {
        int j = i * i;
        for (int k = -i; k <= i; ++k) {
            for (int l = -i; l <= i; ++l) {
                if (k * k + l * l > j) continue;
                this.method_23383(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, abstractTreeFeatureConfig);
            }
        }
    }

    protected void method_23400(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = 0; j < i; ++j) {
            mutable.set(blockPos).setOffset(0, j, 0);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            if (j >= i - 1) continue;
            mutable.set(blockPos).setOffset(1, j, 0);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            mutable.set(blockPos).setOffset(1, j, 1);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            mutable.set(blockPos).setOffset(0, j, 1);
            if (!MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) continue;
            this.method_23382(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
        }
    }
}

