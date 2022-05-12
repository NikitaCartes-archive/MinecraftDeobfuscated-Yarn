/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MiscConfiguredFeatures;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;
import net.minecraft.world.gen.feature.OceanConfiguredFeatures;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.PileConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class ConfiguredFeatures {
    public static RegistryEntry<? extends ConfiguredFeature<?, ?>> getDefaultConfiguredFeature(Registry<ConfiguredFeature<?, ?>> registry) {
        List<RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>>> list = List.of(OceanConfiguredFeatures.KELP, UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL, EndConfiguredFeatures.CHORUS_PLANT, MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD, NetherConfiguredFeatures.BASALT_BLOBS, OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileConfiguredFeatures.PILE_HAY, TreeConfiguredFeatures.AZALEA_TREE, VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
        return Util.getRandom(list, AbstractRandom.createAtomic());
    }

    private static BlockPredicate createBlockPredicate(List<Block> validGround) {
        BlockPredicate blockPredicate = !validGround.isEmpty() ? BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), validGround)) : BlockPredicate.IS_AIR;
        return blockPredicate;
    }

    public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(int tries, RegistryEntry<PlacedFeature> feature) {
        return new RandomPatchFeatureConfig(tries, 7, 3, feature);
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(F feature, FC config, List<Block> predicateBlocks, int tries) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(feature, config, ConfiguredFeatures.createBlockPredicate(predicateBlocks)));
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(F feature, FC config, List<Block> predicateBlocks) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(feature, config, predicateBlocks, 96);
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(F feature, FC config) {
        return ConfiguredFeatures.createRandomPatchFeatureConfig(feature, config, List.of(), 96);
    }

    public static RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> register(String id, Feature<DefaultFeatureConfig> feature) {
        return ConfiguredFeatures.register(id, feature, FeatureConfig.DEFAULT);
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
        return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<FC, F>(feature, config));
    }
}

