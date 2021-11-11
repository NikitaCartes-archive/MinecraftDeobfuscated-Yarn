/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MiscConfiguredFeatures;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.OceanConfiguredFeatures;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.PileConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class ConfiguredFeatures {
    public static ConfiguredFeature<?, ?> getDefaultConfiguredFeature() {
        ConfiguredFeature[] configuredFeatures = new ConfiguredFeature[]{OceanConfiguredFeatures.KELP, UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL, EndConfiguredFeatures.CHORUS_PLANT, MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD, NetherConfiguredFeatures.BASALT_BLOBS, OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileConfiguredFeatures.PILE_HAY, TreeConfiguredFeatures.AZALEA_TREE, VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA};
        return Util.getRandom(configuredFeatures, new Random());
    }

    private static BlockPredicate createBlockPredicate(List<Block> validGround) {
        BlockPredicate blockPredicate = !validGround.isEmpty() ? BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.matchingBlocks(validGround, new BlockPos(0, -1, 0))) : BlockPredicate.IS_AIR;
        return blockPredicate;
    }

    public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(int tries, PlacedFeature feature) {
        return new RandomPatchFeatureConfig(tries, 7, 3, () -> feature);
    }

    public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature, List<Block> validGround, int tries) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, feature.withBlockPredicateFilter(ConfiguredFeatures.createBlockPredicate(validGround)));
    }

    public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature, List<Block> validGround) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(feature, validGround, 96);
    }

    public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(feature, List.of(), 96);
    }

    public static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }
}

