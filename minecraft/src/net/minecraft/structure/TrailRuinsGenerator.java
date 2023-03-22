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
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup2.getOrThrow(StructureProcessorLists.TRAIL_RUINS_SUSPICIOUS_SAND);
		poolRegisterable.register(
			TOWER,
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/tower_3", registryEntry2), 1)
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
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/large_hall_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/platform_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/hall_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/stable_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/tower/one_room_1", registryEntry2), 1)
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
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/long_road_end"), 1),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_end_1"), 2),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_section_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_section_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_section_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_section_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trail_ruins/roads/road_spacer_1"), 1)
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
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_entrance_three_1", registryEntry2), 3),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_entrance_two_1", registryEntry2), 3),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_entrance_two_2", registryEntry2), 3),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/large_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/one_room_4", registryEntry2), 1)
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
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_one_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_one_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_two_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_two_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_two_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_two_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/buildings/group_room_two_5", registryEntry2), 1)
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
					Pair.of(StructurePoolElement.ofProcessedSingle("trail_ruins/decor/decor_6", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
