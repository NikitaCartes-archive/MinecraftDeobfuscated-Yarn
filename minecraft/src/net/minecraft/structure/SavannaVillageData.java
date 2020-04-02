package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;

public class SavannaVillageData {
	public static void initialize() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_PLANKS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_STAIRS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_WOOD, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ORANGE_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.YELLOW_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.RED_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_1"), 100),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_2"), 50),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_3"), 150),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_4"), 150),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_2", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_3", immutableList), 3),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_4", immutableList), 3)
					),
					StructurePool.Projection.RIGID
				)
			);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.ACACIA_PLANKS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRASS_PATH, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/streets"),
					new Identifier("village/savanna/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/streets/corner_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/corner_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_02", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_04", immutableList2), 7),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_05", immutableList2), 3),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_06", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_08", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_09", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_10", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_11", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_04", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_05", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_06", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_07", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/split_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/split_02", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/turn_01", immutableList2), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/zombie/streets"),
					new Identifier("village/savanna/zombie/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/corner_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/corner_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_02", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_04", immutableList2), 7),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_05", immutableList2), 3),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_06", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_08", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_09", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_10", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/straight_11", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_04", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_05", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_06", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/crossroad_07", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/split_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/split_02", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/streets/turn_01", immutableList2), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		ImmutableList<StructureProcessor> immutableList3 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/houses"),
					new Identifier("village/savanna/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_3"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_4"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_5"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_6"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_7"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_house_8"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_medium_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_medium_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_butchers_shop_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_butchers_shop_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_tool_smith_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_fletcher_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_shepherd_1"), 7),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_armorer_1"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_fisher_cottage_1"), 3),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_tannery_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_cartographer_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_library_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_mason_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_2"), 3),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_large_farm_1", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_large_farm_2", immutableList3), 6),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_farm", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_animal_pen_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_animal_pen_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_animal_pen_3"), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 5)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/zombie/houses"),
					new Identifier("village/savanna/zombie/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_3", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_4", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_5", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_6", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_7", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_small_house_8", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_medium_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_medium_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_butchers_shop_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_butchers_shop_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_tool_smith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_fletcher_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_shepherd_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_armorer_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_fisher_cottage_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_tannery_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_cartographer_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_library_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_mason_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_2", immutableList), 3),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_large_farm_1", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_large_farm_2", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_farm", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_animal_pen_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_animal_pen_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/houses/savanna_animal_pen_3", immutableList), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 5)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_01", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_03", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_04", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/savanna/terminators/terminator_05", immutableList2), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/zombie/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_01", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_03", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_04", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/terminators/terminator_05", immutableList2), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/trees"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/savanna_lamp_post_01"), 4),
						new Pair<>(new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), 4),
						new Pair<>(new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), 4),
						new Pair<>(new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.MELON_PILE_CONFIG)), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 4)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/zombie/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/savanna_lamp_post_01", immutableList), 4),
						new Pair<>(new FeaturePoolElement(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), 4),
						new Pair<>(new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), 4),
						new Pair<>(new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.MELON_PILE_CONFIG)), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 4)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/villagers/baby"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/zombie/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/zombie/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/zombie/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
