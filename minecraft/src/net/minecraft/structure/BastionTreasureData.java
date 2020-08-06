package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class BastionTreasureData {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/bases"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/treasure/bases/lava_basin", StructureProcessorLists.TREASURE_ROOMS), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/stairs"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/treasure/stairs/lower_stairs", StructureProcessorLists.TREASURE_ROOMS), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/bases/centers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/bases/centers/center_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/bases/centers/center_1", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/bases/centers/center_2", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/bases/centers/center_3", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/brains"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/treasure/brains/center_brain", StructureProcessorLists.TREASURE_ROOMS), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/lava_wall", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/entrance_wall", StructureProcessorLists.HIGH_WALL), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/walls/outer"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/top_corner", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/mid_corner", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/bottom_corner", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/outer_wall", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/medium_outer_wall", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/outer/tall_outer_wall", StructureProcessorLists.HIGH_WALL), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/walls/bottom"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/bottom/wall_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/bottom/wall_1", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/bottom/wall_2", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/bottom/wall_3", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/walls/mid"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/mid/wall_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/mid/wall_1", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/mid/wall_2", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/walls/top"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/top/main_entrance", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/top/wall_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/walls/top/wall_1", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/connectors"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/connectors/center_to_wall_middle", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/connectors/center_to_wall_top", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/connectors/center_to_wall_top_entrance", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/entrances"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30435("bastion/treasure/entrances/entrance_0", StructureProcessorLists.TREASURE_ROOMS), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/ramparts"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/mid_wall_main", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/mid_wall_side", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/bottom_wall_0", StructureProcessorLists.BOTTOM_RAMPART), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/top_wall", StructureProcessorLists.HIGH_RAMPART), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/lava_basin_side", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/ramparts/lava_basin_main", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/corners/bottom"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/bottom/corner_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/bottom/corner_1", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/corners/edges"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/edges/bottom", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/edges/middle", StructureProcessorLists.HIGH_WALL), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/edges/top", StructureProcessorLists.HIGH_WALL), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/corners/middle"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/middle/corner_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/middle/corner_1", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/corners/top"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/top/corner_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/corners/top/corner_1", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/extensions/large_pool"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/empty", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/empty", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/fire_room", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/large_bridge_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/large_bridge_1", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/large_bridge_2", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/large_bridge_3", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/roofed_bridge", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/empty", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/extensions/small_pool"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/empty", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/fire_room", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/empty", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/small_bridge_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/small_bridge_1", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/small_bridge_2", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/small_bridge_3", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/extensions/houses"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/house_0", StructureProcessorLists.TREASURE_ROOMS), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/extensions/house_1", StructureProcessorLists.TREASURE_ROOMS), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/treasure/roofs"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/roofs/wall_roof", StructureProcessorLists.ROOF), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/roofs/corner_roof", StructureProcessorLists.ROOF), 1),
					Pair.of(StructurePoolElement.method_30435("bastion/treasure/roofs/center_roof", StructureProcessorLists.ROOF), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
