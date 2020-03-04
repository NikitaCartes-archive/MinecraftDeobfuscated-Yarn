/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public abstract class SaplingGenerator {
    @Nullable
    protected abstract ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random var1, boolean var2);

    public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
        ConfiguredFeature<BranchedTreeFeatureConfig, ?> configuredFeature = this.createTreeFeature(random, this.method_24282(iWorld, blockPos));
        if (configuredFeature == null) {
            return false;
        }
        iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
        ((BranchedTreeFeatureConfig)configuredFeature.config).ignoreFluidCheck();
        if (configuredFeature.generate(iWorld, chunkGenerator, random, blockPos)) {
            return true;
        }
        iWorld.setBlockState(blockPos, blockState, 4);
        return false;
    }

    private boolean method_24282(IWorld iWorld, BlockPos blockPos) {
        for (BlockPos blockPos2 : BlockPos.Mutable.iterate(blockPos.down().north(2).west(2), blockPos.up().south(2).east(2))) {
            if (!iWorld.getBlockState(blockPos2).matches(BlockTags.FLOWERS)) continue;
            return true;
        }
        return false;
    }
}

