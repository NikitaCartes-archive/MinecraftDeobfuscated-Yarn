package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;

public class BastionUnitsData {
	public static void init() {
	}

	static {
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/center_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_1", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_2", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/pathways"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/pathways/pathway_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/pathways/pathway_wall_0", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/walls/wall_bases"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/walls/wall_base", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/walls/connected_wall", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_0"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_1", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_2", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_3", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_1"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_1", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_2", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_3", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/rot/stage_1"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/stages/rot/stage_1_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_2"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_2_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_2_1", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/stages/stage_3"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_1", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_2", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_3", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/fillers/stage_0"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/fillers/stage_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/edges"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/edges/edge_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/wall_units"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/wall_units/unit_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/edge_wall_units"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/wall_units/edge_0_large", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_0", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_1", ProcessorLists.HOUSING), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_2", ProcessorLists.HOUSING), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/large_ramparts"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/units/rampart_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/units/rampart_plates/plate_0", ProcessorLists.HOUSING), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
