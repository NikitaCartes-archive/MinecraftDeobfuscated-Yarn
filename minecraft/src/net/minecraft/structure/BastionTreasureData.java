package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class BastionTreasureData {
	public static void init() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
					)
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.35F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.1F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					BastionData.PROCESSOR_RULE
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList3 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.5F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
					)
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList4 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						AlwaysTrueRuleTest.INSTANCE,
						AlwaysTrueRuleTest.INSTANCE,
						new AxisAlignedLinearPosRuleTest(0.0F, 0.05F, 0, 100, Direction.Axis.Y),
						Blocks.AIR.getDefaultState()
					)
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList5 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.MAGMA_BLOCK, 0.75F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, 0.15F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					BastionData.PROCESSOR_RULE
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/starters"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/treasure/big_air_full", immutableList2), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/bases"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/treasure/bases/lava_basin", immutableList2), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/stairs"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/treasure/stairs/lower_stairs", immutableList2), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/bases/centers"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/bases/centers/center_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/bases/centers/center_1", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/bases/centers/center_2", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/bases/centers/center_3", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/brains"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/treasure/brains/center_brain", immutableList2), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/walls"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/walls/lava_wall", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/entrance_wall", immutableList3), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/walls/outer"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/top_corner", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/mid_corner", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/bottom_corner", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/outer_wall", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/medium_outer_wall", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/outer/tall_outer_wall", immutableList3), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/walls/bottom"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/walls/bottom/wall_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/bottom/wall_1", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/bottom/wall_2", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/bottom/wall_3", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/walls/mid"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/walls/mid/wall_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/mid/wall_1", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/mid/wall_2", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/walls/top"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/walls/top/main_entrance", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/top/wall_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/walls/top/wall_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/connectors"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/connectors/center_to_wall_middle", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/connectors/center_to_wall_top", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/connectors/center_to_wall_top_entrance", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/entrances"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/treasure/entrances/entrance_0", immutableList2), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/ramparts"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/mid_wall_main", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/mid_wall_side", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/bottom_wall_0", immutableList5), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/top_wall", immutableList4), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/lava_basin_side", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/ramparts/lava_basin_main", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/corners/bottom"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/corners/bottom/corner_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/corners/bottom/corner_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/corners/edges"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/corners/edges/bottom", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/corners/edges/middle", immutableList3), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/corners/edges/top", immutableList3), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/corners/middle"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/corners/middle/corner_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/corners/middle/corner_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/corners/top"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/corners/top/corner_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/corners/top/corner_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/extensions/large_pool"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/empty", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/empty", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/fire_room", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/large_bridge_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/large_bridge_1", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/large_bridge_2", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/large_bridge_3", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/roofed_bridge", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/empty", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/extensions/small_pool"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/empty", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/fire_room", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/empty", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/small_bridge_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/small_bridge_1", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/small_bridge_2", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/small_bridge_3", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/extensions/houses"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/house_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/extensions/house_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/treasure/roofs"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/treasure/roofs/wall_roof", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/roofs/corner_roof", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/treasure/roofs/center_roof", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
