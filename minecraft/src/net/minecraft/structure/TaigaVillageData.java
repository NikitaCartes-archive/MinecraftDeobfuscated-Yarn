package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_6825;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class TaigaVillageData {
	public static final StructurePool STRUCTURE_POOLS = StructurePools.register(
		new StructurePool(
			new Identifier("village/taiga/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 49),
				Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_2", StructureProcessorLists.MOSSIFY_10_PERCENT), 49),
				Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_1", StructureProcessorLists.ZOMBIE_TAIGA), 1),
				Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_2", StructureProcessorLists.ZOMBIE_TAIGA), 1)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/streets"),
				new Identifier("village/taiga/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/zombie/streets"),
				new Identifier("village/taiga/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/houses"),
				new Identifier("village/taiga/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_3", StructureProcessorLists.MOSSIFY_10_PERCENT), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_4", StructureProcessorLists.MOSSIFY_10_PERCENT), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_5", StructureProcessorLists.MOSSIFY_10_PERCENT), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_2", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_3", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_4", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tool_smith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_shepherds_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_2", StructureProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fisher_cottage_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_cartographer_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_library_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_2", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_temple_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", StructureProcessorLists.FARM_TAIGA), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_2", StructureProcessorLists.FARM_TAIGA), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", StructureProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/zombie/houses"),
				new Identifier("village/taiga/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_2", StructureProcessorLists.ZOMBIE_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_3", StructureProcessorLists.ZOMBIE_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_4", StructureProcessorLists.ZOMBIE_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_5", StructureProcessorLists.ZOMBIE_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_2", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_3", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_4", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_tool_smith_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_shepherds_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_fisher_cottage_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_cartographer_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_library_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_weaponsmith_2", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_temple_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", StructureProcessorLists.ZOMBIE_TAIGA), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_large_farm_2", StructureProcessorLists.ZOMBIE_TAIGA), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", StructureProcessorLists.ZOMBIE_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", StructureProcessorLists.ZOMBIE_TAIGA), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_lamp_post_1"), 10),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_6"), 1),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36202), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36203), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36199), 2),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36206), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36207), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), 1),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36202), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36203), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36199), 2),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36206), 4),
					Pair.of(StructurePoolElement.ofFeature(class_6825.field_36207), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/taiga/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
