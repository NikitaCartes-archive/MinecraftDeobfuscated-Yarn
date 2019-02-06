package net.minecraft.structure.generator.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.sortme.rule.BlockMatchRuleTest;
import net.minecraft.sortme.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SavannaVillageData {
	public static void initialize() {
	}

	static {
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_2"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_3"), 3),
						new Pair<>(new SinglePoolElement("village/savanna/town_centers/savanna_meeting_point_4"), 3)
					),
					StructurePool.Projection.RIGID
				)
			);
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10218.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/streets"),
					new Identifier("village/savanna/terminators"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/savanna/streets/corner_01", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/corner_03", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_02", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_04", immutableList), 7),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_05", immutableList), 3),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_06", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_08", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_09", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_10", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/straight_11", immutableList), 4),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_02", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_03", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_04", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_05", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_06", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/crossroad_07", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/split_01", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/split_02", immutableList), 2),
						new Pair<>(new SinglePoolElement("village/savanna/streets/turn_01", immutableList), 3)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
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
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_shepherd_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_armorer_1"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_fisher_cottage_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_tannery_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_cartographer_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_library_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_mason_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_1"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_weaponsmith_2"), 2),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_1"), 1),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_temple_2"), 3),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_large_farm_1", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_large_farm_2", immutableList2), 4),
						new Pair<>(new SinglePoolElement("village/savanna/houses/savanna_small_farm", immutableList2), 4),
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
					new Identifier("village/savanna/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_01", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_02", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_03", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/plains/terminators/terminator_04", immutableList), 1),
						new Pair<>(new SinglePoolElement("village/savanna/terminators/terminator_05", immutableList), 1)
					),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("village/savanna/trees"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13545, FeatureConfig.DEFAULT)), 1)),
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
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_13545, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_16797, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new FeaturePoolElement(new ConfiguredFeature<>(Feature.field_17007, FeatureConfig.DEFAULT)), 1),
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
						new Pair<>(new SinglePoolElement("village/plains/villagers/nitwit"), 1),
						new Pair<>(new SinglePoolElement("village/plains/villagers/unemployed"), 7),
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
	}
}
