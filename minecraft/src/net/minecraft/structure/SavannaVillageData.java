package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class SavannaVillageData {
	public static final StructurePool field_26285 = TemplatePools.register(
		new StructurePool(
			new Identifier("village/savanna/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_1"), 100),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_2"), 50),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_3"), 150),
				Pair.of(StructurePoolElement.method_30425("village/savanna/town_centers/savanna_meeting_point_4"), 150),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_2", ProcessorLists.ZOMBIE_SAVANNA), 1),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_3", ProcessorLists.ZOMBIE_SAVANNA), 3),
				Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/town_centers/savanna_meeting_point_4", ProcessorLists.ZOMBIE_SAVANNA), 3)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/streets"),
				new Identifier("village/savanna/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/corner_01", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/corner_03", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_02", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_04", ProcessorLists.STREET_SAVANNA), 7),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_05", ProcessorLists.STREET_SAVANNA), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_06", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_08", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_09", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_10", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/straight_11", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_02", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_03", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_04", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_05", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_06", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/crossroad_07", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/split_01", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/split_02", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/streets/turn_01", ProcessorLists.STREET_SAVANNA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/streets"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/corner_01", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/corner_03", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_02", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_04", ProcessorLists.STREET_SAVANNA), 7),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_05", ProcessorLists.STREET_SAVANNA), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_06", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_08", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_09", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_10", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/straight_11", ProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_02", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_03", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_04", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_05", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_06", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/crossroad_07", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/split_01", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/split_02", ProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/streets/turn_01", ProcessorLists.STREET_SAVANNA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
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
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_1", ProcessorLists.FARM_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_2", ProcessorLists.FARM_SAVANNA), 6),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_small_farm", ProcessorLists.FARM_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/savanna/houses/savanna_animal_pen_3"), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/houses"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_2", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_3", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_4", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_5", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_6", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_7", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_small_house_8", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_medium_house_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_medium_house_2", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_butchers_shop_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_butchers_shop_2", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_tool_smith_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_fletcher_house_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_shepherd_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_armorer_1", ProcessorLists.ZOMBIE_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_fisher_cottage_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_tannery_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_cartographer_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_library_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_mason_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_weaponsmith_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_weaponsmith_2", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA), 3),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_large_farm_1", ProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_large_farm_2", ProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_small_farm", ProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30426("village/savanna/houses/savanna_animal_pen_1", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_animal_pen_2", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/houses/savanna_animal_pen_3", ProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/terminators/terminator_05", ProcessorLists.STREET_SAVANNA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", ProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.method_30426("village/savanna/zombie/terminators/terminator_05", ProcessorLists.STREET_SAVANNA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/trees"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.ACACIA), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/savanna_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.ACACIA), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_MELON), 1),
					Pair.of(StructurePoolElement.method_30438(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/savanna/savanna_lamp_post_01", ProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.ACACIA), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_MELON), 1),
					Pair.of(StructurePoolElement.method_30438(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/baby"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/savanna/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/savanna/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
