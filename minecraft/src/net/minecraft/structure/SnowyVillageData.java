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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SnowyVillageData {
	public static void initialize() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_16541), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_9975, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10071, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10436, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10558, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
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
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/town_centers/snowy_meeting_point_1"), 100),
						new Pair<>(new SinglePoolElement("village/snowy/town_centers/snowy_meeting_point_2"), 50),
						new Pair<>(new SinglePoolElement("village/snowy/town_centers/snowy_meeting_point_3"), 150),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_2"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_3"), 3)
					),
					StructurePool.Projection.RIGID
				)
			);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
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
					new Identifier("village/snowy/streets"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/streets/corner_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/corner_02", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/corner_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/square_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_01", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_02", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_03", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_04", immutableList2), 7),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_06", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/streets/straight_08", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/streets/crossroad_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/snowy/streets/crossroad_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/crossroad_04", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/crossroad_05", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/crossroad_06", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/streets/turn_01", immutableList2), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/zombie/streets"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/corner_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/corner_02", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/corner_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/square_01", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_01", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_02", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_03", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_04", immutableList2), 7),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_06", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/straight_08", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/crossroad_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/crossroad_03", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/crossroad_04", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/crossroad_05", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/crossroad_06", immutableList2), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/streets/turn_01", immutableList2), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		ImmutableList<StructureProcessor> immutableList3 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/houses"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_3"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_4"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_5"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_6"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_7"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_small_house_8"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_medium_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_medium_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_medium_house_3"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_butchers_shop_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_butchers_shop_2"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_tool_smith_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_fletcher_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_shepherds_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_armorer_house_1"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_armorer_house_2"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_fisher_cottage"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_tannery_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_cartographer_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_library_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_masons_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_masons_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_weapon_smith_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_temple_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_farm_1", immutableList3), 3),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_farm_2", immutableList3), 3),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_animal_pen_1"), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_animal_pen_2"), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 6)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/zombie/houses"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_3", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_4", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_5", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_6", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_7", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_small_house_8", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_3", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_butchers_shop_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_butchers_shop_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_tool_smith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_fletcher_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_shepherds_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_armorer_house_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_armorer_house_2", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_fisher_cottage", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_tannery_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_cartographer_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_library_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_masons_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_masons_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_weapon_smith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_temple_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_farm_1", immutableList), 3),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_farm_2", immutableList), 3),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_animal_pen_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/snowy/houses/snowy_animal_pen_2", immutableList), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 6)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_01", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_02", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_03", immutableList2), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_04", immutableList2), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/trees"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_01"), 4),
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_02"), 4),
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_03"), 1),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17005, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17006, FeatureConfig.DEFAULT)), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 9)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/zombie/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_01", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_02", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/snowy/snowy_lamp_post_03", immutableList), 1),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17005, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17006, FeatureConfig.DEFAULT)), 4),
						Pair.of(EmptyPoolElement.INSTANCE, 7)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/villagers/baby"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/snowy/zombie/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/snowy/zombie/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/snowy/zombie/villagers/unemployed"), 10)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
