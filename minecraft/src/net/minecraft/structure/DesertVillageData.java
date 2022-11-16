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

public class DesertVillageData {
	public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("village/desert/town_centers");
	private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.of("village/desert/terminators");
	private static final RegistryKey<StructurePool> ZOMBIE_TERMINATORS_KEY = StructurePools.of("village/desert/zombie/terminators");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PATCH_CACTUS);
		RegistryEntry<PlacedFeature> registryEntry2 = registryEntryLookup.getOrThrow(VillagePlacedFeatures.PILE_HAY);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry3 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ZOMBIE_DESERT);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.FARM_DESERT);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry5 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry6 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);
		RegistryEntry<StructurePool> registryEntry7 = registryEntryLookup3.getOrThrow(ZOMBIE_TERMINATORS_KEY);
		poolRegisterable.register(
			TOWN_CENTERS_KEY,
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/town_centers/desert_meeting_point_1"), 98),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/town_centers/desert_meeting_point_2"), 98),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/town_centers/desert_meeting_point_3"), 49),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/town_centers/desert_meeting_point_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/town_centers/desert_meeting_point_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/town_centers/desert_meeting_point_3", registryEntry3), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/streets",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/corner_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/corner_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/straight_01"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/straight_02"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/straight_03"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/crossroad_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/crossroad_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/crossroad_03"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/square_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/square_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/streets/turn_01"), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/zombie/streets",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/corner_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/corner_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/straight_01"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/straight_02"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/straight_03"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/crossroad_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/crossroad_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/crossroad_03"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/square_01"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/square_02"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/streets/turn_01"), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/houses",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_4"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_6"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_7"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_small_house_8"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_medium_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_medium_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_butcher_shop_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_shepherd_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_armorer_1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_fisher_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_tannery_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_cartographer_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_library_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_mason_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_weaponsmith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_temple_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_temple_2"), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_large_farm_1", registryEntry4), 11),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_farm_1", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_farm_2", registryEntry4), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/houses/desert_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/zombie/houses",
			new StructurePool(
				registryEntry7,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_3", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_4", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_5", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_6", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_7", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_small_house_8", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_medium_house_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/zombie/houses/desert_medium_house_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_butcher_shop_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_tool_smith_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_fletcher_house_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_shepherd_house_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_armorer_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_fisher_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_tannery_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_cartographer_house_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_library_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_mason_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_weaponsmith_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_temple_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_temple_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_large_farm_1", registryEntry3), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_farm_1", registryEntry3), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_farm_2", registryEntry3), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_animal_pen_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/houses/desert_animal_pen_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			TERMINATORS_KEY,
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/terminators/terminator_01"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/terminators/terminator_02"), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		poolRegisterable.register(
			ZOMBIE_TERMINATORS_KEY,
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/terminators/terminator_01"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/terminators/terminator_02"), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/decor",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/desert_lamp_1"), 10),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofEmpty(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/zombie/decor",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/desert/desert_lamp_1", registryEntry3), 10),
					Pair.of(StructurePoolElement.ofFeature(registryEntry), 4),
					Pair.of(StructurePoolElement.ofFeature(registryEntry2), 4),
					Pair.of(StructurePoolElement.ofEmpty(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/villagers",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/camel",
			new StructurePool(
				registryEntry5, ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("village/desert/camel_spawn"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"village/desert/zombie/villagers",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/desert/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
