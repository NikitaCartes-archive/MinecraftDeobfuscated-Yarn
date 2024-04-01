package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.VillagePlacedFeatures;
import org.apache.commons.lang3.tuple.Triple;

public class PotatoVillageData {
	public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("village/potato/town_centers");
	private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.of("village/potato/terminators");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(VillagePlacedFeatures.POTATO);
		RegistryEntry<PlacedFeature> registryEntry2 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.FLOWER_PLAIN);
		RegistryEntry<PlacedFeature> registryEntry3 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_POTATO_FRUIT);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.SPOIL_10_PERCENT);
		RegistryEntry<StructureProcessorList> registryEntry5 = registryEntryLookup2.getOrThrow(StructureProcessorLists.SPOIL_20_PERCENT);
		RegistryEntry<StructureProcessorList> registryEntry6 = registryEntryLookup2.getOrThrow(StructureProcessorLists.SPOIL_70_PERCENT);
		RegistryEntry<StructureProcessorList> registryEntry7 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ZOMBIE_POTATO);
		RegistryEntry<StructureProcessorList> registryEntry8 = registryEntryLookup2.getOrThrow(StructureProcessorLists.STREET_POTATO);
		RegistryEntry<StructureProcessorList> registryEntry9 = registryEntryLookup2.getOrThrow(StructureProcessorLists.FARM_POTATO);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry10 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry11 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);
		poolRegisterable.register(
			TOWN_CENTERS_KEY,
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/town_centers/plains_fountain_01", registryEntry5), 50),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/town_centers/plains_meeting_point_1", registryEntry5), 50),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/town_centers/plains_meeting_point_2"), 50),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/town_centers/plains_meeting_point_3", registryEntry6), 50),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/town_centers/plains_fountain_01", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/town_centers/plains_meeting_point_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/town_centers/plains_meeting_point_2", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/town_centers/plains_meeting_point_3", registryEntry7), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/streets",
			new StructurePool(
				ImmutableList.of(
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/corner_01", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/corner_02", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/corner_03", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_01", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_02", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_03", registryEntry8), 7, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_04", registryEntry8), 7, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_05", registryEntry8), 3, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/straight_06", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_01", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_02", registryEntry8), 1, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_03", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_04", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_05", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/crossroad_06", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/streets/turn_01", registryEntry8), 3, StructurePool.Projection.TERRAIN_MATCHING),
					Triple.of(StructurePoolElement.ofLegacySingle("village/potato/houses/potato_maze"), 2, StructurePool.Projection.RIGID)
				),
				registryEntry11
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/zombie/streets",
			new StructurePool(
				ImmutableList.of(
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/corner_01", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/corner_02", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/corner_03", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_01", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_02", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_03", registryEntry8), 7, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_04", registryEntry8), 7, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_05", registryEntry8), 3, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/straight_06", registryEntry8), 4, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_01", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_02", registryEntry8), 1, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_03", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_04", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_05", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/crossroad_06", registryEntry8), 2, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(
						StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/streets/turn_01", registryEntry8), 3, StructurePool.Projection.TERRAIN_MATCHING
					),
					Triple.of(StructurePoolElement.ofLegacySingle("village/potato/houses/potato_maze"), 1, StructurePool.Projection.RIGID)
				),
				registryEntry11
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/houses",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_3", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_4", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_5", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_6", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_7", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_house_8", registryEntry4), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_medium_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_medium_house_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_big_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_butcher_shop_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_butcher_shop_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_tool_smith_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_fletcher_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_shepherds_house_1"), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_armorer_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_fisher_cottage_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_tannery_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_cartographer_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_library_1", registryEntry4), 5),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_library_2", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_masons_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_weaponsmith_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_temple_3", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_temple_4", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_stable_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_stable_2"), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_large_farm_1", registryEntry9), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_farm_1", registryEntry9), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_animal_pen_1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_animal_pen_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_animal_pen_3"), 5),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_accessory_1"), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_meeting_point_4", registryEntry6), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/houses/plains_meeting_point_5"), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/zombie/houses",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_3", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_4", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_5", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_6", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_7", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_small_house_8", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_medium_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_medium_house_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_big_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_butcher_shop_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_butcher_shop_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_tool_smith_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_fletcher_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_shepherds_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_armorer_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_fisher_cottage_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_tannery_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_cartographer_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_library_1", registryEntry7), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_library_2", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_masons_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_weaponsmith_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_temple_3", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_temple_4", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_stable_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_stable_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_large_farm_1", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_small_farm_1", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_animal_pen_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/houses/plains_animal_pen_2", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_animal_pen_3", registryEntry7), 5),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_meeting_point_4", registryEntry7), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/zombie/houses/plains_meeting_point_5", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			TERMINATORS_KEY,
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/terminators/terminator_01", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/terminators/terminator_02", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/terminators/terminator_03", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/terminators/terminator_04", registryEntry8), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/trees",
			new StructurePool(registryEntry10, ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(registryEntry), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/decor",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/plains_lamp_1"), 3),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/frying_table_1"), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/zombie/decor",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/potato/plains_lamp_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/villagers",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/zombie/villagers",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/potato/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/potato/well_bottoms",
			new StructurePool(
				registryEntry10, ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("village/potato/well_bottom"), 1)), StructurePool.Projection.RIGID
			)
		);
	}
}
