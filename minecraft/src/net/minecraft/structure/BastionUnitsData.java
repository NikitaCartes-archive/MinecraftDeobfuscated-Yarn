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

public class BastionUnitsData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.HOUSING);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry2 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/center_pieces",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_2", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/pathways",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/pathways/pathway_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/pathways/pathway_wall_0", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/walls/wall_bases",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/walls/wall_base", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/walls/connected_wall", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/stages/stage_0",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/stages/stage_1",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/stages/rot/stage_1",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/rot/stage_1_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/stages/stage_2",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_2_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_2_1", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/stages/stage_3",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_2", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_3", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/fillers/stage_0",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/fillers/stage_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/edges",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/edges/edge_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/wall_units",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/wall_units/unit_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/edge_wall_units",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/wall_units/edge_0_large", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/ramparts",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_0", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_1", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_2", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/large_ramparts",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/units/rampart_plates",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/rampart_plates/plate_0", registryEntry), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
