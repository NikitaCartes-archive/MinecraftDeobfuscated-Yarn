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

public class BastionHoglinStableData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.STABLE_DEGRADATION);
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup.getOrThrow(StructureProcessorLists.SIDE_WALL_DEGRADATION);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry3 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/starting_pieces",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/starting_stairs_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/starting_stairs_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/starting_stairs_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/starting_stairs_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/starting_stairs_4", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/mirrored_starting_pieces",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/wall_bases",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/walls/wall_base", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/walls",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/walls/side_wall_0", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/walls/side_wall_1", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/stairs",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_1_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_1_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_1_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_1_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_1_4", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_2_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_2_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_2_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_2_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_2_4", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_3_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_3_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_3_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_3_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/stairs/stairs_3_4", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/small_stables/inner",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/inner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/inner_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/inner_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/inner_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/small_stables/outer",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/outer_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/outer_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/outer_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/small_stables/outer_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/large_stables/inner",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/inner_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/inner_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/inner_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/inner_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/inner_4", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/large_stables/outer",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/outer_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/outer_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/outer_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/outer_3", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/large_stables/outer_4", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/posts",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/posts/stair_post", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/posts/end_post", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/ramparts",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/ramparts/ramparts_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/ramparts/ramparts_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/ramparts/ramparts_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/rampart_plates",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/rampart_plates/rampart_plate_1", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/hoglin_stable/connectors",
			new StructurePool(
				registryEntry3,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/connectors/end_post_connector", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
