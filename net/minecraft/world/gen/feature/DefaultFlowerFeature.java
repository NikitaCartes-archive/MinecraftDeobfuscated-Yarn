/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class DefaultFlowerFeature
extends FlowerFeature<RandomPatchFeatureConfig> {
    public DefaultFlowerFeature(Codec<RandomPatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean isPosValid(WorldAccess worldAccess, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return !randomPatchFeatureConfig.blacklist.contains(worldAccess.getBlockState(blockPos));
    }

    @Override
    public int getFlowerAmount(RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return randomPatchFeatureConfig.tries;
    }

    @Override
    public BlockPos getPos(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return blockPos.add(random.nextInt(randomPatchFeatureConfig.spreadX) - random.nextInt(randomPatchFeatureConfig.spreadX), random.nextInt(randomPatchFeatureConfig.spreadY) - random.nextInt(randomPatchFeatureConfig.spreadY), random.nextInt(randomPatchFeatureConfig.spreadZ) - random.nextInt(randomPatchFeatureConfig.spreadZ));
    }

    @Override
    public BlockState getFlowerState(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
    }
}

