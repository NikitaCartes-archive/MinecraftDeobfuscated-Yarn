package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;

public class BastionTreasureData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.TREASURE_ROOMS);
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup.getOrThrow(StructureProcessorLists.HIGH_WALL);
		RegistryEntry<StructureProcessorList> registryEntry3 = registryEntryLookup.getOrThrow(StructureProcessorLists.BOTTOM_RAMPART);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup.getOrThrow(StructureProcessorLists.HIGH_RAMPART);
		RegistryEntry<StructureProcessorList> registryEntry5 = registryEntryLookup.getOrThrow(StructureProcessorLists.ROOF);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry6 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/bases",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/bases/lava_basin", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/stairs",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/stairs/lower_stairs", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/bases/centers",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/bases/centers/center_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/bases/centers/center_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/bases/centers/center_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/bases/centers/center_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/brains",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/brains/center_brain", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/walls",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/lava_wall", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/entrance_wall", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/walls/outer",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/top_corner", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/mid_corner", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/bottom_corner", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/outer_wall", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/medium_outer_wall", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/outer/tall_outer_wall", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/walls/bottom",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/bottom/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/bottom/wall_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/bottom/wall_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/bottom/wall_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/walls/mid",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/mid/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/mid/wall_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/mid/wall_2", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/walls/top",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/top/main_entrance", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/top/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/walls/top/wall_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/connectors",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/connectors/center_to_wall_middle", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/connectors/center_to_wall_top", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/connectors/center_to_wall_top_entrance", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/entrances",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/entrances/entrance_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/ramparts",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/mid_wall_main", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/mid_wall_side", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/bottom_wall_0", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/top_wall", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/lava_basin_side", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/ramparts/lava_basin_main", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/corners/bottom",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/bottom/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/bottom/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/corners/edges",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/edges/bottom", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/edges/middle", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/edges/top", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/corners/middle",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/middle/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/middle/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/corners/top",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/top/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/corners/top/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/extensions/large_pool",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/fire_room", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/large_bridge_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/large_bridge_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/large_bridge_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/large_bridge_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/roofed_bridge", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/empty", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/extensions/small_pool",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/fire_room", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/small_bridge_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/small_bridge_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/small_bridge_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/small_bridge_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/extensions/houses",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/house_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/extensions/house_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/treasure/roofs",
			new StructurePool(
				registryEntry6,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/roofs/wall_roof", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/roofs/corner_roof", registryEntry5), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/roofs/center_roof", registryEntry5), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
