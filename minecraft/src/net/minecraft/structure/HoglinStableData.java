package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;

public class HoglinStableData {
	public static void init() {
	}

	static {
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/starting_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/starting_stairs_4", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/mirrored_starting_pieces"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/wall_bases"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/wall_base", ProcessorLists.STABLE_DEGRADATION), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/side_wall_0", ProcessorLists.SIDE_WALL_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/walls/side_wall_1", ProcessorLists.SIDE_WALL_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/stairs"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_1_4", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_2_4", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/stairs/stairs_3_4", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/small_stables/inner"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/inner_3", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/small_stables/outer"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/small_stables/outer_3", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/large_stables/inner"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/inner_4", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/large_stables/outer"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_0", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_3", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/large_stables/outer_4", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/posts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/posts/stair_post", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/posts/end_post", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_1", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_2", ProcessorLists.STABLE_DEGRADATION), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/ramparts/ramparts_3", ProcessorLists.STABLE_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/rampart_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/rampart_plates/rampart_plate_1", ProcessorLists.STABLE_DEGRADATION), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("bastion/hoglin_stable/connectors"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/connectors/end_post_connector", ProcessorLists.STABLE_DEGRADATION), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
