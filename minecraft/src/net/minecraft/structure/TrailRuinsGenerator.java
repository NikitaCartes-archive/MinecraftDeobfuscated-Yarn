package net.minecraft.structure;

import com.mojang.datafixers.util.Pair;
import java.util.List;
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

public class TrailRuinsGenerator {
	public static final RegistryKey<StructurePool> TOWER = StructurePools.of("trail_ruins/tower");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry = registryEntryLookup.getOrThrow(StructurePools.EMPTY);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup2.getOrThrow(StructureProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY);
		RegistryEntry<StructureProcessorList> registryEntry3 = registryEntryLookup2.getOrThrow(StructureProcessorLists.TRAIL_RUINS_ROADS_ARCHAEOLOGY);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.TRAIL_RUINS_TOWER_TOP_ARCHAEOLOGY);
		poolRegisterable.register(
			TOWER,
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_5", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/tower/tower_top",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_top_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_top_2", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_top_3", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_top_4", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_top_5", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/tower/additions",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_5", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/roads",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/long_road_end", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_end_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_section_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_section_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_section_3", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_section_4", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/roads/road_spacer_1", registryEntry3), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/buildings",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_hall_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_hall_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_hall_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_hall_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_hall_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_5", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/buildings/grouped",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_full_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_full_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_full_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_full_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_full_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_lower_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_lower_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_lower_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_lower_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_lower_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_upper_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_upper_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_upper_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_upper_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_upper_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_5", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trail_ruins/decor",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_5", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_6", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_7", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
