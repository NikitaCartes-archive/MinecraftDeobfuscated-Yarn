package net.minecraft.structure.generator.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.sortme.rule.BlockMatchRuleTest;
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

public class DesertVillageData {
	public static void initialize() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10467, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10361, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10415, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10549, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10262, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/town_centers/desert_meeting_point_1"), 98),
						new Pair<>(new SinglePoolElement("village/desert/town_centers/desert_meeting_point_2"), 98),
						new Pair<>(new SinglePoolElement("village/desert/town_centers/desert_meeting_point_3"), 49),
						new Pair<>(new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/streets"),
					new Identifier("village/desert/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/streets/corner_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/corner_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/straight_01"), 4),
						new Pair<>(new SinglePoolElement("village/desert/streets/straight_02"), 4),
						new Pair<>(new SinglePoolElement("village/desert/streets/straight_03"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/crossroad_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/crossroad_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/crossroad_03"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/square_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/square_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/streets/turn_01"), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/zombie/streets"),
					new Identifier("village/desert/zombie/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/corner_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/corner_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/straight_01"), 4),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/straight_02"), 4),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/straight_03"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/crossroad_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/crossroad_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/crossroad_03"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/square_01"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/square_02"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/streets/turn_01"), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/houses"),
					new Identifier("village/desert/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_3"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_4"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_5"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_6"), 1),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_7"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_small_house_8"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_medium_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_medium_house_2"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_butcher_shop_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_tool_smith_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_fletcher_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_shepherd_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_armorer_1"), 1),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_fisher_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_tannery_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_cartographer_house_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_library_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_mason_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_weaponsmith_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_temple_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_temple_2"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_large_farm_1", immutableList2), 7),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_farm_1", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_farm_2", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_animal_pen_1"), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_animal_pen_2"), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 5)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/zombie/houses"),
					new Identifier("village/desert/zombie/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_3", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_4", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_5", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_6", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_7", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_small_house_8", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_medium_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/houses/desert_medium_house_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_butcher_shop_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_tool_smith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_fletcher_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_shepherd_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_armorer_1", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_fisher_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_tannery_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_cartographer_house_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_library_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_mason_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_weaponsmith_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_temple_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_temple_2", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_large_farm_1", immutableList), 7),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_farm_1", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_farm_2", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_animal_pen_1", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/desert/houses/desert_animal_pen_2", immutableList), 2),
						Pair.of(EmptyPoolElement.INSTANCE, 5)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/terminators/terminator_01"), 1),
						new Pair<>(new SinglePoolElement("village/desert/terminators/terminator_02"), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/zombie/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/terminators/terminator_01"), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/terminators/terminator_02"), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/desert_lamp_1"), 10),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13554, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_16797, FeatureConfig.DEFAULT)), 4),
						Pair.of(EmptyPoolElement.INSTANCE, 10)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/zombie/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/desert_lamp_1", immutableList), 10),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13554, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_16797, FeatureConfig.DEFAULT)), 4),
						Pair.of(EmptyPoolElement.INSTANCE, 10)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/unemployed"), 7),
						Pair.of(EmptyPoolElement.INSTANCE, 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/armorer"), 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/butcher"), 2),
						new Pair<>(new SinglePoolElement("village/plains/villagers/cartographer"), 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/cleric"), 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/farmer"), 8),
						new Pair<>(new SinglePoolElement("village/plains/villagers/fishermen"), 3),
						new Pair<>(new SinglePoolElement("village/plains/villagers/fletcher"), 2),
						new Pair<>(new SinglePoolElement("village/plains/villagers/leatherworker"), 3),
						new Pair<>(new SinglePoolElement("village/plains/villagers/librarian"), 2),
						new Pair<>(new SinglePoolElement("village/plains/villagers/shepherd"), 3),
						new Pair<>(new SinglePoolElement("village/plains/villagers/toolsmith"), 4),
						new Pair<>(new SinglePoolElement("village/plains/villagers/weaponsmith"), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/desert/zombie/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/unemployed"), 7),
						Pair.of(EmptyPoolElement.INSTANCE, 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/armorer"), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/butcher"), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/cartographer"), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/cleric"), 1),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/farmer"), 8),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/fishermen"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/fletcher"), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/leatherworker"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/librarian"), 2),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/shepherd"), 3),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/toolsmith"), 4),
						new Pair<>(new SinglePoolElement("village/desert/zombie/villagers/weaponsmith"), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
