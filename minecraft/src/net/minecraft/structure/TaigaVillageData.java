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

public class TaigaVillageData {
	public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("village/taiga/town_centers");
	private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.of("village/taiga/terminators");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(VillagePlacedFeatures.SPRUCE);
		RegistryEntry<PlacedFeature> registryEntry2 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PINE);
		RegistryEntry<PlacedFeature> registryEntry3 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_PUMPKIN);
		RegistryEntry<PlacedFeature> registryEntry4 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PATCH_TAIGA_GRASS);
		RegistryEntry<PlacedFeature> registryEntry5 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PATCH_BERRY_BUSH);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry6 = registryEntryLookup2.getOrThrow(StructureProcessorLists.MOSSIFY_10_PERCENT);
		RegistryEntry<StructureProcessorList> registryEntry7 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ZOMBIE_TAIGA);
		RegistryEntry<StructureProcessorList> registryEntry8 = registryEntryLookup2.getOrThrow(StructureProcessorLists.STREET_SNOWY_OR_TAIGA);
		RegistryEntry<StructureProcessorList> registryEntry9 = registryEntryLookup2.getOrThrow(StructureProcessorLists.FARM_TAIGA);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry10 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry11 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);
		poolRegisterable.register(
			TOWN_CENTERS_KEY,
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_1", registryEntry6), 49),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/town_centers/taiga_meeting_point_2", registryEntry6), 49),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/town_centers/taiga_meeting_point_2", registryEntry7), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/streets",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_01", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_02", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/corner_03", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_01", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_02", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_03", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_04", registryEntry8), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_05", registryEntry8), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/straight_06", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_01", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_02", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_03", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_04", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_05", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/crossroad_06", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/streets/turn_01", registryEntry8), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/zombie/streets",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_01", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_02", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/corner_03", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_01", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_02", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_03", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_04", registryEntry8), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_05", registryEntry8), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/straight_06", registryEntry8), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_01", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_02", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_03", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_04", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_05", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/crossroad_06", registryEntry8), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/streets/turn_01", registryEntry8), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/houses",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_1", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_2", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_3", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_4", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_house_5", registryEntry6), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_3", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_medium_house_4", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tool_smith_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_shepherds_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_2", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fisher_cottage_1", registryEntry6), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_cartographer_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_library_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_temple_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", registryEntry9), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_2", registryEntry9), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/zombie/houses",
			new StructurePool(
				registryEntry11,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_1", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_2", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_3", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_4", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_small_house_5", registryEntry7), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_3", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_medium_house_4", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_butcher_shop_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_tool_smith_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_fletcher_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_shepherds_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_armorer_house_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_fisher_cottage_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_tannery_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_cartographer_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_library_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_masons_house_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_weaponsmith_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_weaponsmith_2", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_temple_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_large_farm_1", registryEntry7), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/zombie/houses/taiga_large_farm_2", registryEntry7), 6),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_small_farm_1", registryEntry7), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/taiga/houses/taiga_animal_pen_1", registryEntry7), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			TERMINATORS_KEY,
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", registryEntry8), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", registryEntry8), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/decor",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_lamp_post_1"), 10),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_6"), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 2),
					Pair.of(StructurePoolElement.ofFeature(registryEntry4), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry5), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/zombie/decor",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_1"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_3"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/taiga_decoration_4"), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 2),
					Pair.of(StructurePoolElement.ofFeature(registryEntry4), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry5), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 4)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/villagers",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/taiga/zombie/villagers",
			new StructurePool(
				registryEntry10,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/taiga/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
