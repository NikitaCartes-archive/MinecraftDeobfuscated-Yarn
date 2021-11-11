package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;

public class OreConfiguredFeatures {
	public static final RuleTest BASE_STONE_OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
	public static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
	public static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
	public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.NETHERRACK);
	public static final RuleTest BASE_STONE_NETHER = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
	public static final List<OreFeatureConfig.Target> IRON_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.IRON_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_IRON_ORE.getDefaultState())
	);
	public static final List<OreFeatureConfig.Target> OVERWORLD_GOLD_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.GOLD_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_GOLD_ORE.getDefaultState())
	);
	public static final List<OreFeatureConfig.Target> DIAMOND_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.DIAMOND_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
	);
	public static final List<OreFeatureConfig.Target> LAPIS_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.LAPIS_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState())
	);
	public static final List<OreFeatureConfig.Target> COPPER_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.COPPER_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COPPER_ORE.getDefaultState())
	);
	public static final List<OreFeatureConfig.Target> COAL_ORES = List.of(
		OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.COAL_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COAL_ORE.getDefaultState())
	);
	public static final ConfiguredFeature<?, ?> ORE_MAGMA = ConfiguredFeatures.register(
		"ore_magma", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33))
	);
	public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = ConfiguredFeatures.register(
		"ore_soul_sand", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.SOUL_SAND.getDefaultState(), 12))
	);
	public static final ConfiguredFeature<?, ?> ORE_NETHER_GOLD = ConfiguredFeatures.register(
		"ore_nether_gold", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.NETHER_GOLD_ORE.getDefaultState(), 10))
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ = ConfiguredFeatures.register(
		"ore_quartz", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14))
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = ConfiguredFeatures.register(
		"ore_gravel_nether", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.GRAVEL.getDefaultState(), 33))
	);
	public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = ConfiguredFeatures.register(
		"ore_blackstone", Feature.ORE.configure(new OreFeatureConfig(NETHERRACK, Blocks.BLACKSTONE.getDefaultState(), 33))
	);
	public static final ConfiguredFeature<?, ?> ORE_DIRT = ConfiguredFeatures.register(
		"ore_dirt", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.DIRT.getDefaultState(), 33))
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL = ConfiguredFeatures.register(
		"ore_gravel", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.GRAVEL.getDefaultState(), 33))
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE = ConfiguredFeatures.register(
		"ore_granite", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.GRANITE.getDefaultState(), 64))
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE = ConfiguredFeatures.register(
		"ore_diorite", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.DIORITE.getDefaultState(), 64))
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE = ConfiguredFeatures.register(
		"ore_andesite", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.ANDESITE.getDefaultState(), 64))
	);
	public static final ConfiguredFeature<?, ?> ORE_TUFF = ConfiguredFeatures.register(
		"ore_tuff", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.TUFF.getDefaultState(), 64))
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL = ConfiguredFeatures.register("ore_coal", Feature.ORE.configure(new OreFeatureConfig(COAL_ORES, 17)));
	public static final ConfiguredFeature<?, ?> ORE_COAL_BURIED = ConfiguredFeatures.register(
		"ore_coal_buried", Feature.ORE.configure(new OreFeatureConfig(COAL_ORES, 17, 0.5F))
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON = ConfiguredFeatures.register("ore_iron", Feature.ORE.configure(new OreFeatureConfig(IRON_ORES, 9)));
	public static final ConfiguredFeature<?, ?> ORE_IRON_SMALL = ConfiguredFeatures.register(
		"ore_iron_small", Feature.ORE.configure(new OreFeatureConfig(IRON_ORES, 4))
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD = ConfiguredFeatures.register(
		"ore_gold", Feature.ORE.configure(new OreFeatureConfig(OVERWORLD_GOLD_ORES, 9))
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_BURIED = ConfiguredFeatures.register(
		"ore_gold_buried", Feature.ORE.configure(new OreFeatureConfig(OVERWORLD_GOLD_ORES, 9, 0.5F))
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE = ConfiguredFeatures.register(
		"ore_redstone",
		Feature.ORE
			.configure(
				new OreFeatureConfig(
					List.of(
						OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.REDSTONE_ORE.getDefaultState()),
						OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState())
					),
					8
				)
			)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND_SMALL = ConfiguredFeatures.register(
		"ore_diamond_small", Feature.ORE.configure(new OreFeatureConfig(DIAMOND_ORES, 4, 0.5F))
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND_LARGE = ConfiguredFeatures.register(
		"ore_diamond_large", Feature.ORE.configure(new OreFeatureConfig(DIAMOND_ORES, 12, 0.7F))
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND_BURIED = ConfiguredFeatures.register(
		"ore_diamond_buried", Feature.ORE.configure(new OreFeatureConfig(DIAMOND_ORES, 8, 1.0F))
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS = ConfiguredFeatures.register("ore_lapis", Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORES, 7)));
	public static final ConfiguredFeature<?, ?> ORE_LAPIS_BURIED = ConfiguredFeatures.register(
		"ore_lapis_buried", Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORES, 7, 1.0F))
	);
	public static final ConfiguredFeature<?, ?> ORE_INFESTED = ConfiguredFeatures.register(
		"ore_infested",
		Feature.ORE
			.configure(
				new OreFeatureConfig(
					List.of(
						OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.INFESTED_STONE.getDefaultState()),
						OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.INFESTED_DEEPSLATE.getDefaultState())
					),
					9
				)
			)
	);
	public static final ConfiguredFeature<?, ?> ORE_EMERALD = ConfiguredFeatures.register(
		"ore_emerald",
		Feature.ORE
			.configure(
				new OreFeatureConfig(
					List.of(
						OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, Blocks.EMERALD_ORE.getDefaultState()),
						OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState())
					),
					3
				)
			)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANCIENT_DEBRIS_LARGE = ConfiguredFeatures.register(
		"ore_ancient_debris_large", Feature.SCATTERED_ORE.configure(new OreFeatureConfig(BASE_STONE_NETHER, Blocks.ANCIENT_DEBRIS.getDefaultState(), 3, 1.0F))
	);
	public static final ConfiguredFeature<?, ?> ORE_ANCIENT_DEBRIS_SMALL = ConfiguredFeatures.register(
		"ore_ancient_debris_small", Feature.SCATTERED_ORE.configure(new OreFeatureConfig(BASE_STONE_NETHER, Blocks.ANCIENT_DEBRIS.getDefaultState(), 2, 1.0F))
	);
	public static final ConfiguredFeature<?, ?> ORE_COPPER_SMALL = ConfiguredFeatures.register(
		"ore_copper_small", Feature.ORE.configure(new OreFeatureConfig(COPPER_ORES, 10))
	);
	public static final ConfiguredFeature<?, ?> ORE_COPPER_LARGE = ConfiguredFeatures.register(
		"ore_copper_large", Feature.ORE.configure(new OreFeatureConfig(COPPER_ORES, 20))
	);
	public static final ConfiguredFeature<?, ?> ORE_CLAY = ConfiguredFeatures.register(
		"ore_clay", Feature.ORE.configure(new OreFeatureConfig(BASE_STONE_OVERWORLD, Blocks.CLAY.getDefaultState(), 33))
	);
}
