package net.minecraft.world.gen.features.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_3776;
import net.minecraft.class_3777;
import net.minecraft.class_3778;
import net.minecraft.class_3781;
import net.minecraft.class_3785;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DesertVillageData {
	public static void method_17037() {
	}

	static {
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/desert/town_centers/desert_meeting_point_1"), 3),
						new Pair<>(new class_3781("village/desert/town_centers/desert_meeting_point_2"), 3),
						new Pair<>(new class_3781("village/desert/town_centers/desert_meeting_point_3"), 1)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/streets"),
					new Identifier("village/desert/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/desert/streets/corner_01"), 3),
						new Pair<>(new class_3781("village/desert/streets/corner_02"), 3),
						new Pair<>(new class_3781("village/desert/streets/straight_01"), 4),
						new Pair<>(new class_3781("village/desert/streets/straight_02"), 4),
						new Pair<>(new class_3781("village/desert/streets/straight_03"), 3),
						new Pair<>(new class_3781("village/desert/streets/crossroad_01"), 3),
						new Pair<>(new class_3781("village/desert/streets/crossroad_02"), 3),
						new Pair<>(new class_3781("village/desert/streets/crossroad_03"), 3),
						new Pair<>(new class_3781("village/desert/streets/square_01"), 3),
						new Pair<>(new class_3781("village/desert/streets/square_02"), 3),
						new Pair<>(new class_3781("village/desert/streets/turn_01"), 3)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/houses"),
					new Identifier("village/desert/terminators"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_2"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_3"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_4"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_5"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_6"), 1),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_7"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_small_house_8"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_medium_house_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_medium_house_2"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_butcher_shop_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_tool_smith_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_fletcher_house_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_shepherd_house_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_armorer_1"), 1),
						new Pair<>(new class_3781("village/desert/houses/desert_fisher_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_tannery_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_cartographer_house_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_library_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_mason_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_weaponsmith_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_temple_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_temple_2"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_large_farm_1"), 6),
						new Pair<>(new class_3781("village/desert/houses/desert_farm_1"), 4),
						new Pair<>(new class_3781("village/desert/houses/desert_farm_2"), 4),
						new Pair<>(new class_3781("village/desert/houses/desert_animal_pen_1"), 2),
						new Pair<>(new class_3781("village/desert/houses/desert_animal_pen_2"), 2),
						Pair.of(class_3777.field_16663, 5)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/desert/terminators/terminator_01"), 1), new Pair<>(new class_3781("village/desert/terminators/terminator_02"), 1)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/desert/desert_lamp_1"), 10),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13554, FeatureConfig.DEFAULT)), 4),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_16797, FeatureConfig.DEFAULT)), 4),
						Pair.of(class_3777.field_16663, 10)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/desert/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/villagers/nitwit"), 1),
						new Pair<>(new class_3781("village/plains/villagers/unemployed"), 7),
						Pair.of(class_3777.field_16663, 1),
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
