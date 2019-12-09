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
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class MegaTreeFeature<T extends TreeFeatureConfig>
extends AbstractTreeFeature<T> {
    public MegaTreeFeature(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected int getHeight(Random random, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        int i = random.nextInt(3) + megaTreeFeatureConfig.baseHeight;
        if (megaTreeFeatureConfig.heightInterval > 1) {
            i += random.nextInt(megaTreeFeatureConfig.heightInterval);
        }
        return i;
    }

    private boolean doesTreeFit(TestableWorld world, BlockPos pos, int height) {
        boolean bl = true;
        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) {
            return false;
        }
        for (int i = 0; i <= 1 + height; ++i) {
            int j = 2;
            if (i == 0) {
                j = 1;
            } else if (i >= 1 + height - 2) {
                j = 2;
            }
            for (int k = -j; k <= j && bl; ++k) {
                for (int l = -j; l <= j && bl; ++l) {
                    if (pos.getY() + i >= 0 && pos.getY() + i < 256 && MegaTreeFeature.canTreeReplace(world, pos.add(k, i, l))) continue;
                    bl = false;
                }
            }
        }
        return bl;
    }

    private boolean replaceGround(ModifiableTestableWorld world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        if (!MegaTreeFeature.isNaturalDirtOrGrass(world, blockPos) || pos.getY() < 2) {
            return false;
        }
        this.setToDirt(world, blockPos);
        this.setToDirt(world, blockPos.east());
        this.setToDirt(world, blockPos.south());
        this.setToDirt(world, blockPos.south().east());
        return true;
    }

    protected boolean checkTreeFitsAndReplaceGround(ModifiableTestableWorld world, BlockPos pos, int height) {
        return this.doesTreeFit(world, pos, height) && this.replaceGround(world, pos);
    }

    protected void makeSquaredLeafLayer(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig) {
        int j = i * i;
        for (int k = -i; k <= i + 1; ++k) {
            for (int l = -i; l <= i + 1; ++l) {
                int n;
                int m = Math.min(Math.abs(k), Math.abs(k - 1));
                if (m + (n = Math.min(Math.abs(l), Math.abs(l - 1))) >= 7 || m * m + n * n > j) continue;
                this.setLeavesBlockState(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, treeFeatureConfig);
            }
        }
    }

    protected void makeRoundLeafLayer(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig) {
        int j = i * i;
        for (int k = -i; k <= i; ++k) {
            for (int l = -i; l <= i; ++l) {
                if (k * k + l * l > j) continue;
                this.setLeavesBlockState(modifiableTestableWorld, random, blockPos.add(k, 0, l), set, blockBox, treeFeatureConfig);
            }
        }
    }

    protected void method_23400(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = 0; j < i; ++j) {
            mutable.set(blockPos).setOffset(0, j, 0);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.setLogBlockState(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            if (j >= i - 1) continue;
            mutable.set(blockPos).setOffset(1, j, 0);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.setLogBlockState(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            mutable.set(blockPos).setOffset(1, j, 1);
            if (MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
                this.setLogBlockState(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
            }
            mutable.set(blockPos).setOffset(0, j, 1);
            if (!MegaTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) continue;
            this.setLogBlockState(modifiableTestableWorld, random, mutable, set, blockBox, megaTreeFeatureConfig);
        }
    }
}

