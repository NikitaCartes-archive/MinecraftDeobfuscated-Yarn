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

public class BastionUnitsData {
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
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					BastionData.PROCESSOR_RULE
				)
			)
		);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/base"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/air_base", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/center_pieces"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/center_pieces/center_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/center_pieces/center_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/center_pieces/center_2", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/pathways"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/pathways/pathway_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/pathways/pathway_wall_0", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/walls/wall_bases"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/walls/wall_base", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/walls/connected_wall", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/stages/stage_0"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_0_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_0_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_0_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_0_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/stages/stage_1"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_1_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_1_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_1_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_1_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/stages/rot/stage_1"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/stages/rot/stage_1_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/stages/stage_2"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_2_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_2_1", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/stages/stage_3"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_3_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_3_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_3_2", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/stages/stage_3_3", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/fillers/stage_0"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/fillers/stage_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/edges"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/edges/edge_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/wall_units"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/wall_units/unit_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/edge_wall_units"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/wall_units/edge_0_large", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/ramparts"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/units/ramparts/ramparts_0", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/ramparts/ramparts_1", immutableList), 1),
						Pair.of(new SinglePoolElement("bastion/units/ramparts/ramparts_2", immutableList), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/large_ramparts"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/ramparts/ramparts_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/units/rampart_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/units/rampart_plates/plate_0", immutableList), 1)),
					StructurePool.Projection.RIGID
				)
			);
	}
}
