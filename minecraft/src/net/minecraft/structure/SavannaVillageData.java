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

public class SavannaVillageData {
	public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("village/savanna/town_centers");
	private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.of("village/savanna/terminators");
	private static final RegistryKey<StructurePool> ZOMBIE_TERMINATORS_KEY = StructurePools.of("village/savanna/zombie/terminators");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(VillagePlacedFeatures.ACACIA);
		RegistryEntry<PlacedFeature> registryEntry2 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_HAY);
		RegistryEntry<PlacedFeature> registryEntry3 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_MELON);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ZOMBIE_SAVANNA);
		RegistryEntry<StructureProcessorList> registryEntry5 = registryEntryLookup2.getOrThrow(StructureProcessorLists.STREET_SAVANNA);
		RegistryEntry<StructureProcessorList> registryEntry6 = registryEntryLookup2.getOrThrow(StructureProcessorLists.FARM_SAVANNA);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry7 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry8 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);
		RegistryEntry<StructurePool> registryEntry9 = registryEntryLookup3.getOrThrow(ZOMBIE_TERMINATORS_KEY);
		poolRegisterable.register(
			TOWN_CENTERS_KEY,
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_1"), 100),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_2"), 50),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_3"), 150),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/town_centers/savanna_meeting_point_4"), 150),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_2", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_3", registryEntry4), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/town_centers/savanna_meeting_point_4", registryEntry4), 3)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/streets",
			new StructurePool(
				registryEntry8,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/corner_01", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/corner_03", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_02", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_04", registryEntry5), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_05", registryEntry5), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_06", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_08", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_09", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_10", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/straight_11", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_02", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_03", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_04", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_05", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_06", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/crossroad_07", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/split_01", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/split_02", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/streets/turn_01", registryEntry5), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/zombie/streets",
			new StructurePool(
				registryEntry9,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/corner_01", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/corner_03", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_02", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_04", registryEntry5), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_05", registryEntry5), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_06", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_08", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_09", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_10", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/straight_11", registryEntry5), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_02", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_03", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_04", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_05", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_06", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/crossroad_07", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/split_01", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/split_02", registryEntry5), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/streets/turn_01", registryEntry5), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/houses",
			new StructurePool(
				registryEntry8,
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
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_1", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_2", registryEntry6), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_small_farm", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/houses/savanna_animal_pen_3"), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/zombie/houses",
			new StructurePool(
				registryEntry9,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_3", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_4", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_5", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_6", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_7", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_small_house_8", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_medium_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_medium_house_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_butchers_shop_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_butchers_shop_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_tool_smith_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_fletcher_house_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_shepherd_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_armorer_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_fisher_cottage_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_tannery_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_cartographer_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_library_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_mason_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_weaponsmith_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_weaponsmith_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_temple_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_temple_2", registryEntry4), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_large_farm_1", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_large_farm_2", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_small_farm", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/houses/savanna_animal_pen_1", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_animal_pen_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/houses/savanna_animal_pen_3", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			TERMINATORS_KEY,
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/terminators/terminator_05", registryEntry5), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		poolRegisterable.register(
			ZOMBIE_TERMINATORS_KEY,
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/zombie/terminators/terminator_05", registryEntry5), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/trees",
			new StructurePool(registryEntry7, ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(registryEntry), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/decor",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/savanna_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/zombie/decor",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/savanna/savanna_lamp_post_01", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/villagers",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/savanna/zombie/villagers",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/savanna/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
