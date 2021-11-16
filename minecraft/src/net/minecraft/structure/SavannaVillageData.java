package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_6825;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class SavannaVillageData {
	public static final StructurePool STRUCTURE_POOLS = StructurePools.register(
		new StructurePool(
			new Identifier("village/savanna/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_1"), 100),
				Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_2"), 50),
				Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_3"), 150),
				Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_4"), 150),
				Pair.of(
					StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2
				),
				Pair.of(
					StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_2", StructureProcessorLists.ZOMBIE_SAVANNA), 1
				),
				Pair.of(
					StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_3", StructureProcessorLists.ZOMBIE_SAVANNA), 3
				),
				Pair.of(
					StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_4", StructureProcessorLists.ZOMBIE_SAVANNA), 3
				)
			),
			StructurePool.Projection.RIGID
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
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/corner_01", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/corner_03", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_02", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_04", StructureProcessorLists.STREET_SAVANNA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_05", StructureProcessorLists.STREET_SAVANNA), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_06", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_08", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_09", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_10", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_11", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_02", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_03", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_04", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_05", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_06", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_07", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/split_01", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/split_02", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/turn_01", StructureProcessorLists.STREET_SAVANNA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/streets"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/corner_01", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/corner_03", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_02", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_04", StructureProcessorLists.STREET_SAVANNA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_05", StructureProcessorLists.STREET_SAVANNA), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_06", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_08", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_09", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_10", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_11", StructureProcessorLists.STREET_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_02", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_03", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_04", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_05", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_06", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_07", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/split_01", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/split_02", StructureProcessorLists.STREET_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/turn_01", StructureProcessorLists.STREET_SAVANNA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/houses"),
				new Identifier("village/savanna/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_4"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_6"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_7"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_small_house_8"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_medium_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_medium_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_butchers_shop_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_butchers_shop_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_shepherd_1"), 7),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_armorer_1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_fisher_cottage_1"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_tannery_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_cartographer_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_library_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_mason_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_weaponsmith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_weaponsmith_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_temple_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_temple_2"), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_1", StructureProcessorLists.FARM_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_2", StructureProcessorLists.FARM_SAVANNA), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_small_farm", StructureProcessorLists.FARM_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_3"), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/houses"),
				new Identifier("village/savanna/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_2", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_3", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_4", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_5", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_6", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_7", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_8", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_medium_house_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_medium_house_2", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_butchers_shop_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_butchers_shop_2", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_tool_smith_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_fletcher_house_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_shepherd_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_armorer_1", StructureProcessorLists.ZOMBIE_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_fisher_cottage_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_tannery_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_cartographer_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_library_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_mason_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_weaponsmith_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_weaponsmith_2", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_temple_1", StructureProcessorLists.ZOMBIE_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_temple_2", StructureProcessorLists.ZOMBIE_SAVANNA), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_1", StructureProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_large_farm_2", StructureProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_small_farm", StructureProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_animal_pen_1", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_animal_pen_2", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_animal_pen_3", StructureProcessorLists.ZOMBIE_SAVANNA), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/terminators/terminator_05", StructureProcessorLists.STREET_SAVANNA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_SAVANNA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/terminators/terminator_05", StructureProcessorLists.STREET_SAVANNA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/trees"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(class_6825.field_36201), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/savanna_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36201), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36195), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36196), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/savanna_lamp_post_01", StructureProcessorLists.ZOMBIE_SAVANNA), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36201), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36195), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36196), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/savanna/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
