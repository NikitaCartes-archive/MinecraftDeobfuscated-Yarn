package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class BastionUnitsData {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/center_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_1", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/center_pieces/center_2", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/pathways"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/pathways/pathway_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/pathways/pathway_wall_0", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/walls/wall_bases"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/walls/wall_base", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/walls/connected_wall", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_0"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_1", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_2", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_0_3", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_1"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_1", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_2", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_1_3", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/rot/stage_1"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/rot/stage_1_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_2"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_2_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_2_1", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_3"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_1", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_2", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/stages/stage_3_3", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/fillers/stage_0"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/fillers/stage_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/edges"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/edges/edge_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/wall_units"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/wall_units/unit_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/edge_wall_units"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/wall_units/edge_0_large", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_0", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_1", StructureProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_2", StructureProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/large_ramparts"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/ramparts/ramparts_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/units/rampart_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/rampart_plates/plate_0", StructureProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
