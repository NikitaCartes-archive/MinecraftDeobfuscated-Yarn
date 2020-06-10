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
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class HoglinStableData {
	public static void init() {
	}

	static {
		ImmutableList<StructureProcessor> immutableList = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.1F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					BastionData.PROCESSOR_RULE
				)
			)
		);
		ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					BastionData.PROCESSOR_RULE
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/origin"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/hoglin_stable/air_base", ImmutableList.of()), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/starting_pieces"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_4", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/mirrored_starting_pieces"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/wall_bases"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/hoglin_stable/walls/wall_base", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/walls"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/walls/side_wall_0", immutableList2), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/walls/side_wall_1", immutableList2), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/stairs"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_4", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_4", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_4", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/small_stables/inner"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/small_stables/outer"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/large_stables/inner"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_4", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/large_stables/outer"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_3", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_4", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/posts"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/posts/stair_post", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/posts/end_post", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/ramparts"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/rampart_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/hoglin_stable/rampart_plates/rampart_plate_1", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/hoglin_stable/connectors"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/hoglin_stable/connectors/end_post_connector", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
	}
}
