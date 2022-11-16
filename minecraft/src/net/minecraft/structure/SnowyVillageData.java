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

public class SnowyVillageData {
	public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("village/snowy/town_centers");
	private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.of("village/snowy/terminators");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(VillagePlacedFeatures.SPRUCE);
		RegistryEntry<PlacedFeature> registryEntry2 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_SNOW);
		RegistryEntry<PlacedFeature> registryEntry3 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_ICE);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.STREET_SNOWY_OR_TAIGA);
		RegistryEntry<StructureProcessorList> registryEntry5 = registryEntryLookup2.getOrThrow(StructureProcessorLists.FARM_SNOWY);
		RegistryEntry<StructureProcessorList> registryEntry6 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ZOMBIE_SNOWY);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry7 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry8 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);
		poolRegisterable.register(
			TOWN_CENTERS_KEY,
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_1"), 100),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_2"), 50),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_3"), 150),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_3"), 3)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/streets",
			new StructurePool(
				registryEntry8,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_01", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_02", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_03", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/square_01", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_01", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_02", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_03", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_04", registryEntry4), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_06", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_08", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_02", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_03", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_04", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_05", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_06", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/turn_01", registryEntry4), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/zombie/streets",
			new StructurePool(
				registryEntry8,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_01", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_02", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_03", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/square_01", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_01", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_02", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_03", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_04", registryEntry4), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_06", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_08", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_02", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_03", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_04", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_05", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_06", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/turn_01", registryEntry4), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/houses",
			new StructurePool(
				registryEntry8,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_4"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_6"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_7"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_8"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_butchers_shop_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_butchers_shop_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_shepherds_house_1"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_armorer_house_1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_armorer_house_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_fisher_cottage"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_tannery_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_cartographer_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_library_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_masons_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_masons_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_weapon_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_temple_1"), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_1", registryEntry5), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_2", registryEntry5), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/zombie/houses",
			new StructurePool(
				registryEntry8,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_3", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_4", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_5", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_6", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_7", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_8", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_3", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_butchers_shop_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_butchers_shop_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_tool_smith_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_fletcher_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_shepherds_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_armorer_house_1", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_armorer_house_2", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_fisher_cottage", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_tannery_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_cartographer_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_library_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_masons_house_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_masons_house_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_weapon_smith_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_temple_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_1", registryEntry6), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_2", registryEntry6), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_animal_pen_1", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_animal_pen_2", registryEntry6), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			TERMINATORS_KEY,
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", registryEntry4), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/trees",
			new StructurePool(registryEntry7, ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(registryEntry), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/decor",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_02"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_03"), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 9)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/zombie/decor",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_01", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_02", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_03", registryEntry6), 1),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry3), 4),
					Pair.of(StructurePoolElement.ofEmpty(), 7)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/villagers",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/snowy/zombie/villagers",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
