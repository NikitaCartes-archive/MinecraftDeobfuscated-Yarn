package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;

public class ColosseumTreasureData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntry<StructureProcessorList> registryEntry = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST)
			.getOrThrow(StructureProcessorLists.COLOSSEUM_VEINS);
		RegistryEntry<StructurePool> registryEntry2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL).getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/bases",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/bases/lava_basin", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/stairs",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/stairs/lower_stairs", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/bases/centers",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/bases/centers/center_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/bases/centers/center_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/bases/centers/center_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/bases/centers/center_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/brains",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/brains/center_brain", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/walls",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/lava_wall", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/entrance_wall", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/walls/outer",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/top_corner", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/mid_corner", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/bottom_corner", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/outer_wall", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/medium_outer_wall", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/outer/tall_outer_wall", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/walls/bottom",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/bottom/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/bottom/wall_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/bottom/wall_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/bottom/wall_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/walls/mid",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/mid/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/mid/wall_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/mid/wall_2", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/walls/top",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/top/main_entrance", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/top/wall_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/walls/top/wall_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/connectors",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/connectors/center_to_wall_middle", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/connectors/center_to_wall_top", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/connectors/center_to_wall_top_entrance", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/corners/bottom",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/bottom/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/bottom/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/corners/edges",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/edges/bottom", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/edges/middle", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/edges/top", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/corners/middle",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/middle/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/middle/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/corners/top",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/top/corner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/corners/top/corner_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/extensions/large_pool",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/fire_room", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/large_bridge_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/large_bridge_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/large_bridge_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/large_bridge_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/roofed_bridge", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/empty", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/extensions/small_pool",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/fire_room", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/empty", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/small_bridge_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/small_bridge_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/small_bridge_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/small_bridge_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/extensions/houses",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/house_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/extensions/house_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/treasure/roofs",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/roofs/wall_roof", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/roofs/corner_roof", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("colosseum/treasure/roofs/center_roof", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
