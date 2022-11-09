package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;

public class OreConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_MAGMA = ConfiguredFeatures.of("ore_magma");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_SOUL_SAND = ConfiguredFeatures.of("ore_soul_sand");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_NETHER_GOLD = ConfiguredFeatures.of("ore_nether_gold");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_QUARTZ = ConfiguredFeatures.of("ore_quartz");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_GRAVEL_NETHER = ConfiguredFeatures.of("ore_gravel_nether");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_BLACKSTONE = ConfiguredFeatures.of("ore_blackstone");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_DIRT = ConfiguredFeatures.of("ore_dirt");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_GRAVEL = ConfiguredFeatures.of("ore_gravel");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_GRANITE = ConfiguredFeatures.of("ore_granite");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_DIORITE = ConfiguredFeatures.of("ore_diorite");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_ANDESITE = ConfiguredFeatures.of("ore_andesite");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_TUFF = ConfiguredFeatures.of("ore_tuff");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_COAL = ConfiguredFeatures.of("ore_coal");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_COAL_BURIED = ConfiguredFeatures.of("ore_coal_buried");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_IRON = ConfiguredFeatures.of("ore_iron");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_IRON_SMALL = ConfiguredFeatures.of("ore_iron_small");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_GOLD = ConfiguredFeatures.of("ore_gold");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_GOLD_BURIED = ConfiguredFeatures.of("ore_gold_buried");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_REDSTONE = ConfiguredFeatures.of("ore_redstone");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_SMALL = ConfiguredFeatures.of("ore_diamond_small");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_LARGE = ConfiguredFeatures.of("ore_diamond_large");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_BURIED = ConfiguredFeatures.of("ore_diamond_buried");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_LAPIS = ConfiguredFeatures.of("ore_lapis");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_LAPIS_BURIED = ConfiguredFeatures.of("ore_lapis_buried");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_INFESTED = ConfiguredFeatures.of("ore_infested");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_EMERALD = ConfiguredFeatures.of("ore_emerald");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_LARGE = ConfiguredFeatures.of("ore_ancient_debris_large");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_SMALL = ConfiguredFeatures.of("ore_ancient_debris_small");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_COPPER_SMALL = ConfiguredFeatures.of("ore_copper_small");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE = ConfiguredFeatures.of("ore_copper_large");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_CLAY = ConfiguredFeatures.of("ore_clay");

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		RuleTest ruleTest = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
		RuleTest ruleTest2 = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
		RuleTest ruleTest3 = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
		RuleTest ruleTest4 = new BlockMatchRuleTest(Blocks.NETHERRACK);
		RuleTest ruleTest5 = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
		List<OreFeatureConfig.Target> list = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.IRON_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_IRON_ORE.getDefaultState())
		);
		List<OreFeatureConfig.Target> list2 = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.GOLD_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_GOLD_ORE.getDefaultState())
		);
		List<OreFeatureConfig.Target> list3 = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.DIAMOND_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
		);
		List<OreFeatureConfig.Target> list4 = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.LAPIS_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState())
		);
		List<OreFeatureConfig.Target> list5 = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.COPPER_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_COPPER_ORE.getDefaultState())
		);
		List<OreFeatureConfig.Target> list6 = List.of(
			OreFeatureConfig.createTarget(ruleTest2, Blocks.COAL_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_COAL_ORE.getDefaultState())
		);
		ConfiguredFeatures.register(featureRegisterable, ORE_MAGMA, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.MAGMA_BLOCK.getDefaultState(), 33));
		ConfiguredFeatures.register(featureRegisterable, ORE_SOUL_SAND, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.SOUL_SAND.getDefaultState(), 12));
		ConfiguredFeatures.register(featureRegisterable, ORE_NETHER_GOLD, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.NETHER_GOLD_ORE.getDefaultState(), 10));
		ConfiguredFeatures.register(featureRegisterable, ORE_QUARTZ, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14));
		ConfiguredFeatures.register(featureRegisterable, ORE_GRAVEL_NETHER, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.GRAVEL.getDefaultState(), 33));
		ConfiguredFeatures.register(featureRegisterable, ORE_BLACKSTONE, Feature.ORE, new OreFeatureConfig(ruleTest4, Blocks.BLACKSTONE.getDefaultState(), 33));
		ConfiguredFeatures.register(featureRegisterable, ORE_DIRT, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.DIRT.getDefaultState(), 33));
		ConfiguredFeatures.register(featureRegisterable, ORE_GRAVEL, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.GRAVEL.getDefaultState(), 33));
		ConfiguredFeatures.register(featureRegisterable, ORE_GRANITE, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.GRANITE.getDefaultState(), 64));
		ConfiguredFeatures.register(featureRegisterable, ORE_DIORITE, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.DIORITE.getDefaultState(), 64));
		ConfiguredFeatures.register(featureRegisterable, ORE_ANDESITE, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.ANDESITE.getDefaultState(), 64));
		ConfiguredFeatures.register(featureRegisterable, ORE_TUFF, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.TUFF.getDefaultState(), 64));
		ConfiguredFeatures.register(featureRegisterable, ORE_COAL, Feature.ORE, new OreFeatureConfig(list6, 17));
		ConfiguredFeatures.register(featureRegisterable, ORE_COAL_BURIED, Feature.ORE, new OreFeatureConfig(list6, 17, 0.5F));
		ConfiguredFeatures.register(featureRegisterable, ORE_IRON, Feature.ORE, new OreFeatureConfig(list, 9));
		ConfiguredFeatures.register(featureRegisterable, ORE_IRON_SMALL, Feature.ORE, new OreFeatureConfig(list, 4));
		ConfiguredFeatures.register(featureRegisterable, ORE_GOLD, Feature.ORE, new OreFeatureConfig(list2, 9));
		ConfiguredFeatures.register(featureRegisterable, ORE_GOLD_BURIED, Feature.ORE, new OreFeatureConfig(list2, 9, 0.5F));
		ConfiguredFeatures.register(
			featureRegisterable,
			ORE_REDSTONE,
			Feature.ORE,
			new OreFeatureConfig(
				List.of(
					OreFeatureConfig.createTarget(ruleTest2, Blocks.REDSTONE_ORE.getDefaultState()),
					OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState())
				),
				8
			)
		);
		ConfiguredFeatures.register(featureRegisterable, ORE_DIAMOND_SMALL, Feature.ORE, new OreFeatureConfig(list3, 4, 0.5F));
		ConfiguredFeatures.register(featureRegisterable, ORE_DIAMOND_LARGE, Feature.ORE, new OreFeatureConfig(list3, 12, 0.7F));
		ConfiguredFeatures.register(featureRegisterable, ORE_DIAMOND_BURIED, Feature.ORE, new OreFeatureConfig(list3, 8, 1.0F));
		ConfiguredFeatures.register(featureRegisterable, ORE_LAPIS, Feature.ORE, new OreFeatureConfig(list4, 7));
		ConfiguredFeatures.register(featureRegisterable, ORE_LAPIS_BURIED, Feature.ORE, new OreFeatureConfig(list4, 7, 1.0F));
		ConfiguredFeatures.register(
			featureRegisterable,
			ORE_INFESTED,
			Feature.ORE,
			new OreFeatureConfig(
				List.of(
					OreFeatureConfig.createTarget(ruleTest2, Blocks.INFESTED_STONE.getDefaultState()),
					OreFeatureConfig.createTarget(ruleTest3, Blocks.INFESTED_DEEPSLATE.getDefaultState())
				),
				9
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			ORE_EMERALD,
			Feature.ORE,
			new OreFeatureConfig(
				List.of(
					OreFeatureConfig.createTarget(ruleTest2, Blocks.EMERALD_ORE.getDefaultState()),
					OreFeatureConfig.createTarget(ruleTest3, Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState())
				),
				3
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable, ORE_ANCIENT_DEBRIS_LARGE, Feature.SCATTERED_ORE, new OreFeatureConfig(ruleTest5, Blocks.ANCIENT_DEBRIS.getDefaultState(), 3, 1.0F)
		);
		ConfiguredFeatures.register(
			featureRegisterable, ORE_ANCIENT_DEBRIS_SMALL, Feature.SCATTERED_ORE, new OreFeatureConfig(ruleTest5, Blocks.ANCIENT_DEBRIS.getDefaultState(), 2, 1.0F)
		);
		ConfiguredFeatures.register(featureRegisterable, ORE_COPPER_SMALL, Feature.ORE, new OreFeatureConfig(list5, 10));
		ConfiguredFeatures.register(featureRegisterable, ORE_COPPER_LARGE, Feature.ORE, new OreFeatureConfig(list5, 20));
		ConfiguredFeatures.register(featureRegisterable, ORE_CLAY, Feature.ORE, new OreFeatureConfig(ruleTest, Blocks.CLAY.getDefaultState(), 33));
	}
}
