/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class DefaultFlowerFeature
extends FlowerFeature<RandomPatchFeatureConfig> {
    public DefaultFlowerFeature(Function<Dynamic<?>, ? extends RandomPatchFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean method_23369(IWorld iWorld, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return !randomPatchFeatureConfig.blacklist.contains(iWorld.getBlockState(blockPos));
    }

    @Override
    public int method_23370(RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return randomPatchFeatureConfig.tries;
    }

    @Override
    public BlockPos method_23371(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return blockPos.add(random.nextInt(randomPatchFeatureConfig.spreadX) - random.nextInt(randomPatchFeatureConfig.spreadX), random.nextInt(randomPatchFeatureConfig.spreadY) - random.nextInt(randomPatchFeatureConfig.spreadY), random.nextInt(randomPatchFeatureConfig.spreadZ) - random.nextInt(randomPatchFeatureConfig.spreadZ));
    }

    @Override
    public BlockState getFlowerToPlace(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
        return randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
    }
}

