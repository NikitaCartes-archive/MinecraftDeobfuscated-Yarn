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

public class SnowyVillageData {
	public static void method_16845() {
	}

	static {
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/snowy/town_centers/snowy_meeting_point_1"), 2),
						new Pair<>(new class_3781("village/snowy/town_centers/snowy_meeting_point_2"), 1),
						new Pair<>(new class_3781("village/snowy/town_centers/snowy_meeting_point_3"), 3)
					),
					class_3785.Projection.RIGID
				)
			);
		ImmutableList<AbstractStructureProcessor> immutableList = ImmutableList.of(
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
					new Identifier("village/snowy/streets"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/snowy/streets/corner_01", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/corner_02", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/corner_03", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/square_01", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/straight_01", immutableList), 4),
						new Pair<>(new class_3781("village/snowy/streets/straight_02", immutableList), 4),
						new Pair<>(new class_3781("village/snowy/streets/straight_03", immutableList), 4),
						new Pair<>(new class_3781("village/snowy/streets/straight_04", immutableList), 7),
						new Pair<>(new class_3781("village/snowy/streets/straight_06", immutableList), 4),
						new Pair<>(new class_3781("village/snowy/streets/straight_08", immutableList), 4),
						new Pair<>(new class_3781("village/snowy/streets/crossroad_02", immutableList), 1),
						new Pair<>(new class_3781("village/snowy/streets/crossroad_03", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/crossroad_04", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/crossroad_05", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/crossroad_06", immutableList), 2),
						new Pair<>(new class_3781("village/snowy/streets/turn_01", immutableList), 3)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/houses"),
					new Identifier("village/snowy/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_2"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_3"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_4"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_5"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_6"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_7"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_small_house_8"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_medium_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_medium_house_2"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_medium_house_3"), 1),
						new Pair<>(new class_3781("village/snowy/houses/snowy_butchers_shop_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_butchers_shop_2"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_tool_smith_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_fletcher_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_shepherds_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_armorer_house_1"), 1),
						new Pair<>(new class_3781("village/snowy/houses/snowy_armorer_house_2"), 1),
						new Pair<>(new class_3781("village/snowy/houses/snowy_fisher_cottage"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_tannery_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_cartographer_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_library_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_masons_house_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_masons_house_2"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_weapon_smith_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_temple_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_farm_1"), 3),
						new Pair<>(new class_3781("village/snowy/houses/snowy_farm_2"), 3),
						new Pair<>(new class_3781("village/snowy/houses/snowy_animal_pen_1"), 2),
						new Pair<>(new class_3781("village/snowy/houses/snowy_animal_pen_2"), 2),
						Pair.of(class_3777.field_16663, 6)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/terminators/terminator_01", immutableList), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_02", immutableList), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_03", immutableList), 1),
						new Pair<>(new class_3781("village/plains/terminators/terminator_04", immutableList), 1)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/trees"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 1)),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/snowy/snowy_lamp_post_01"), 4),
						new Pair<>(new class_3781("village/snowy/snowy_lamp_post_02"), 4),
						new Pair<>(new class_3781("village/snowy/snowy_lamp_post_03"), 1),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13577, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_17005, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_17006, FeatureConfig.DEFAULT)), 1),
						Pair.of(class_3777.field_16663, 9)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/snowy/villagers"),
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
