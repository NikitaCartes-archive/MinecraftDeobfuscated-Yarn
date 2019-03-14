package net.minecraft.structure.generator.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.sortme.rule.BlockMatchRuleTest;
import net.minecraft.sortme.rule.BlockStateMatchRuleTest;
import net.minecraft.sortme.rule.RandomBlockMatchRuleTest;
import net.minecraft.sortme.rule.TagMatchRuleTest;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class TaigaVillageData {
	public static void initialize() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState()),
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(
						new BlockMatchRuleTest(Blocks.field_17350),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_17350.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(false))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10037, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10285, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(
							Blocks.field_10285.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
						),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9984.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/town_centers/taiga_meeting_point_1", immutableList2), 49),
						new Pair<>(new SinglePoolElement("village/taiga/town_centers/taiga_meeting_point_2", immutableList2), 49),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/town_centers/taiga_meeting_point_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/town_centers/taiga_meeting_point_2", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		ImmutableList<StructureProcessor> immutableList3 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_9975.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/streets"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/streets/corner_01", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/corner_02", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/corner_03", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_01", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_02", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_03", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_04", immutableList3), 7),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_05", immutableList3), 7),
						new Pair<>(new SinglePoolElement("village/taiga/streets/straight_06", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_01", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_02", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_03", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_04", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_05", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/crossroad_06", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/streets/turn_01", immutableList3), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/zombie/streets"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/corner_01", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/corner_02", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/corner_03", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_01", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_02", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_03", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_04", immutableList3), 7),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_05", immutableList3), 7),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/straight_06", immutableList3), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_01", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_02", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_03", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_04", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_05", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/crossroad_06", immutableList3), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/streets/turn_01", immutableList3), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		ImmutableList<StructureProcessor> immutableList4 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9984.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/houses"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_house_1", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_house_2", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_house_3", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_house_4", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_house_5", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_medium_house_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_medium_house_2", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_medium_house_3", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_medium_house_4", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_butcher_shop_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_tool_smith_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_fletcher_house_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_shepherds_house_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_armorer_house_1", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_armorer_2", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_fisher_cottage_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_tannery_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_cartographer_house_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_library_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_masons_house_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_2", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_temple_1", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_large_farm_1", immutableList4), 6),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_large_farm_2", immutableList4), 6),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_farm_1", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_animal_pen_1", immutableList2), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 6)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/zombie/houses"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_1", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_2", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_3", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_4", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_small_house_5", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_3", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_medium_house_4", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_butcher_shop_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_tool_smith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_fletcher_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_shepherds_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_armorer_house_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_fisher_cottage_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_tannery_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_cartographer_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_library_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_masons_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_weaponsmith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_weaponsmith_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_temple_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_large_farm_1", immutableList), 6),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/houses/taiga_large_farm_2", immutableList), 6),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_small_farm_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/taiga/houses/taiga_animal_pen_1", immutableList), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 6)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_01", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_02", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_03", immutableList3), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_04", immutableList3), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/taiga_lamp_post_1"), 10),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_1"), 4),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_2"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_3"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_4"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_5"), 2),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_6"), 1),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13581, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17106, FeatureConfig.DEFAULT)), 2),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13521, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17004, FeatureConfig.DEFAULT)), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 4)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/zombie/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_1"), 4),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_2"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_3"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/taiga_decoration_4"), 1),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13581, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17106, FeatureConfig.DEFAULT)), 2),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13521, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17004, FeatureConfig.DEFAULT)), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 4)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/villagers/nitwit"), 1), new Pair<>(new SinglePoolElement("village/taiga/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/taiga/zombie/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/taiga/zombie/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/taiga/zombie/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
