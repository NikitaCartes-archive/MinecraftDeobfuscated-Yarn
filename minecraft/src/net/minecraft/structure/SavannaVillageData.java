package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class SavannaVillageData {
	public static final StructurePool field_26285 = StructurePools.register(
		new StructurePool(
			new Identifier("village/savanna/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_1"), 100),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_2"), 50),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_3"), 150),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_4"), 150),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_1", StructureProcessorLists.field_26260), 2),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_2", StructureProcessorLists.field_26260), 1),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_3", StructureProcessorLists.field_26260), 3),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_4", StructureProcessorLists.field_26260), 3)
			),
			StructurePool.Projection.field_16687
		)
	);

	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/streets"),
				new Identifier("village/savanna/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/corner_01", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/corner_03", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_02", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_04", StructureProcessorLists.field_26268), 7),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_05", StructureProcessorLists.field_26268), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_06", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_08", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_09", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_10", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_11", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_02", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_03", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_04", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_05", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_06", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_07", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/split_01", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/split_02", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/turn_01", StructureProcessorLists.field_26268), 3)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/streets"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/corner_01", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/corner_03", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_02", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_04", StructureProcessorLists.field_26268), 7),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_05", StructureProcessorLists.field_26268), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_06", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_08", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_09", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_10", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_11", StructureProcessorLists.field_26268), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_02", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_03", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_04", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_05", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_06", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_07", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/split_01", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/split_02", StructureProcessorLists.field_26268), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/turn_01", StructureProcessorLists.field_26268), 3)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/houses"),
				new Identifier("village/savanna/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_3"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_4"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_5"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_6"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_7"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_small_house_8"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_medium_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_medium_house_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_butchers_shop_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_butchers_shop_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_shepherd_1"), 7),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_armorer_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_fisher_cottage_1"), 3),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_tannery_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_cartographer_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_library_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_mason_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_weaponsmith_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_weaponsmith_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_temple_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_temple_2"), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_1", StructureProcessorLists.field_26271), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_2", StructureProcessorLists.field_26271), 6),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_small_farm", StructureProcessorLists.field_26271), 4),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_3"), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/houses"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_2", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_3", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_4", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_5", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_6", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_7", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_8", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_medium_house_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_medium_house_2", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_butchers_shop_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_butchers_shop_2", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_tool_smith_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_fletcher_house_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_shepherd_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_armorer_1", StructureProcessorLists.field_26260), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_fisher_cottage_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_tannery_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_cartographer_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_library_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_mason_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_weaponsmith_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_weaponsmith_2", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_temple_1", StructureProcessorLists.field_26260), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_temple_2", StructureProcessorLists.field_26260), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_1", StructureProcessorLists.field_26260), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_large_farm_2", StructureProcessorLists.field_26260), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_small_farm", StructureProcessorLists.field_26260), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_animal_pen_1", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_animal_pen_2", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_animal_pen_3", StructureProcessorLists.field_26260), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/terminators/terminator_05", StructureProcessorLists.field_26268), 1)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", StructureProcessorLists.field_26268), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/terminators/terminator_05", StructureProcessorLists.field_26268), 1)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/trees"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26039), 1)),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/savanna_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26039), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26009), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26010), 1),
					Pair.of(StructurePoolElement.method_30438(), 4)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/savanna_lamp_post_01", StructureProcessorLists.field_26260), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26039), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26009), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26010), 1),
					Pair.of(StructurePoolElement.method_30438(), 4)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/baby"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/unemployed"), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
	}
}
