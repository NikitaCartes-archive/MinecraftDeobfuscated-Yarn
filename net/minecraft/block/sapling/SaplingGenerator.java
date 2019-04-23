/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

public abstract class SaplingGenerator {
    @Nullable
    protected abstract AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random var1);

    public boolean generate(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
        AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature = this.createTreeFeature(random);
        if (abstractTreeFeature == null) {
            return false;
        }
        iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
        if (abstractTreeFeature.generate(iWorld, iWorld.getChunkManager().getChunkGenerator(), random, blockPos, FeatureConfig.DEFAULT)) {
            return true;
        }
        iWorld.setBlockState(blockPos, blockState, 4);
        return false;
    }
}

