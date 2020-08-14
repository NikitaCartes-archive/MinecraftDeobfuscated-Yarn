package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class HoglinStableData {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/starting_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_4", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/mirrored_starting_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/wall_bases"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/wall_base", StructureProcessorLists.STABLE_DEGRADATION), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/side_wall_0", StructureProcessorLists.SIDE_WALL_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/side_wall_1", StructureProcessorLists.SIDE_WALL_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/stairs"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_4", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_4", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_4", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/small_stables/inner"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_3", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/small_stables/outer"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_3", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/large_stables/inner"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_4", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/large_stables/outer"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_0", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_3", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_4", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/posts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/posts/stair_post", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/posts/end_post", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_1", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_2", StructureProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_3", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/rampart_plates"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/rampart_plates/rampart_plate_1", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/connectors"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/connectors/end_post_connector", StructureProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
