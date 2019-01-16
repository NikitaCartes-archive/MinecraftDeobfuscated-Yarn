package net.minecraft.world.gen.features.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_3776;
import net.minecraft.class_3777;
import net.minecraft.class_3778;
import net.minecraft.class_3781;
import net.minecraft.class_3785;
import net.minecraft.class_3821;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.sortme.rule.BlockMatchRuleTest;
import net.minecraft.sortme.rule.RandomBlockMatchRuleTest;
import net.minecraft.sortme.structures.processor.AbstractStructureProcessor;
import net.minecraft.sortme.structures.processor.RuleStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class TaigaVillageData {
	public static void method_17038() {
	}

	static {
		ImmutableList<AbstractStructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState()))
			)
		);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/taiga/town_centers/taiga_meeting_point_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/town_centers/taiga_meeting_point_2", immutableList), 2)
					),
					class_3785.Projection.RIGID
				)
			);
		ImmutableList<AbstractStructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_9975.getDefaultState()),
					new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/streets"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/taiga/streets/corner_01", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/corner_02", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/corner_03", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/straight_01", immutableList2), 4),
						new Pair<>(new class_3781("village/taiga/streets/straight_02", immutableList2), 4),
						new Pair<>(new class_3781("village/taiga/streets/straight_03", immutableList2), 4),
						new Pair<>(new class_3781("village/taiga/streets/straight_04", immutableList2), 7),
						new Pair<>(new class_3781("village/taiga/streets/straight_05", immutableList2), 7),
						new Pair<>(new class_3781("village/taiga/streets/straight_06", immutableList2), 4),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_01", immutableList2), 1),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_02", immutableList2), 1),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_03", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_04", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_05", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/crossroad_06", immutableList2), 2),
						new Pair<>(new class_3781("village/taiga/streets/turn_01", immutableList2), 3)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/houses"),
					new Identifier("village/taiga/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_house_1", immutableList), 4),
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_house_2", immutableList), 4),
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_house_3", immutableList), 4),
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_house_4", immutableList), 4),
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_house_5", immutableList), 4),
						new Pair<>(new class_3781("village/taiga/houses/taiga_medium_house_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_medium_house_2", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_medium_house_3", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_butcher_shop_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_tool_smith_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_fletcher_house_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_shepherds_house_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_armorer_house_1", immutableList), 1),
						new Pair<>(new class_3781("village/taiga/houses/taiga_armorer_house_2", immutableList), 1),
						new Pair<>(new class_3781("village/taiga/houses/taiga_fisher_cottage_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_tannery_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_cartographer_house_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_library_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_masons_house_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_weaponsmith_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_weaponsmith_2", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_temple_1", immutableList), 2),
						new Pair<>(new class_3781("village/taiga/houses/taiga_large_farm_1", immutableList), 5),
						new Pair<>(new class_3781("village/taiga/houses/taiga_large_farm_2", immutableList), 5),
						new Pair<>(new class_3781("village/taiga/houses/taiga_small_farm_1", immutableList), 1),
						new Pair<>(new class_3781("village/taiga/houses/taiga_animal_pen_1", immutableList), 2),
						Pair.of(class_3777.field_16663, 6)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/terminators/terminator_01", immutableList2), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_02", immutableList2), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_03", immutableList2), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_04", immutableList2), 1)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/taiga/taiga_lamp_post_1"), 10),
						new Pair<>(new class_3781("village/taiga/taiga_decoration_1"), 4),
						new Pair<>(new class_3781("village/taiga/taiga_decoration_2"), 1),
						new Pair<>(new class_3781("village/taiga/taiga_decoration_3"), 1),
						new Pair<>(new class_3781("village/taiga/taiga_decoration_4"), 1),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13581, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_17106, FeatureConfig.DEFAULT)), 2),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13521, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_17004, FeatureConfig.DEFAULT)), 1),
						Pair.of(class_3777.field_16663, 4)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/taiga/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/villagers/nitwit"), 1),
						new Pair<>(new class_3781("village/plains/villagers/unemployed"), 7),
						Pair.of(class_3777.field_16663, 2),
						new Pair<>(new class_3781("village/plains/villagers/armorer"), 1),
						new Pair<>(new class_3781("village/plains/villagers/butcher"), 2),
						new Pair<>(new class_3781("village/plains/villagers/cartographer"), 1),
						new Pair<>(new class_3781("village/plains/villagers/cleric"), 1),
						new Pair<>(new class_3781("village/plains/villagers/farmer"), 8),
						new Pair<>(new class_3781("village/plains/villagers/fishermen"), 3),
						new Pair<>(new class_3781("village/plains/villagers/fletcher"), 2),
						new Pair<>(new class_3781("village/plains/villagers/leatherworker"), 3),
						new Pair<>(new class_3781("village/plains/villagers/librarian"), 2),
						new Pair<>(new class_3781("village/plains/villagers/shepherd"), 3),
						new Pair<>(new class_3781("village/plains/villagers/toolsmith"), 4),
						new Pair<>(new class_3781("village/plains/villagers/weaponsmith"), 1)
					),
					class_3785.Projection.RIGID
				)
			);
	}
}
