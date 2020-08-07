package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class BastionBridgeData {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/starting_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance", StructureProcessorLists.field_26282), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_face", StructureProcessorLists.field_26280), 1)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/bridge_pieces"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/bridge/bridge_pieces/bridge", StructureProcessorLists.field_26283), 1)),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/legs"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_0", StructureProcessorLists.field_26280), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_1", StructureProcessorLists.field_26280), 1)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_0", StructureProcessorLists.field_26281), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_1", StructureProcessorLists.field_26281), 1)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_0", StructureProcessorLists.field_26281), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_1", StructureProcessorLists.field_26281), 1)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/rampart_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/bridge/rampart_plates/plate_0", StructureProcessorLists.field_26281), 1)),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/bridge/connectors"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_top", StructureProcessorLists.field_26280), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_bottom", StructureProcessorLists.field_26280), 1)
				),
				StructurePool.Projection.field_16687
			)
		);
	}
}
