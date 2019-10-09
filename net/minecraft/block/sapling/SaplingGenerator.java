/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_4640;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public abstract class SaplingGenerator {
    @Nullable
    protected abstract ConfiguredFeature<class_4640, ?> createTreeFeature(Random var1);

    public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
        ConfiguredFeature<class_4640, ?> configuredFeature = this.createTreeFeature(random);
        if (configuredFeature == null) {
            return false;
        }
        iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
        if (configuredFeature.generate(iWorld, chunkGenerator, random, blockPos)) {
            return true;
        }
        iWorld.setBlockState(blockPos, blockState, 4);
        return false;
    }
}

