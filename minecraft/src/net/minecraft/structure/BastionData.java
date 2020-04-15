package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class BastionData {
	public static final StructureProcessorRule PROCESSOR_RULE = new StructureProcessorRule(
		new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.GILDED_BLACKSTONE.getDefaultState()
	);

	public static void init() {
	}

	static {
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/mobs/piglin"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/mobs/melee_piglin"), 1),
						Pair.of(new SinglePoolElement("bastion/mobs/sword_piglin"), 4),
						Pair.of(new SinglePoolElement("bastion/mobs/crossbow_piglin"), 4),
						Pair.of(new SinglePoolElement("bastion/mobs/empty"), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/mobs/hoglin"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/mobs/hoglin"), 2), Pair.of(new SinglePoolElement("bastion/mobs/empty"), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/blocks/gold"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("bastion/blocks/air"), 3), Pair.of(new SinglePoolElement("bastion/blocks/gold"), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("bastion/mobs/piglin_melee"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("bastion/mobs/melee_piglin_always"), 1),
						Pair.of(new SinglePoolElement("bastion/mobs/melee_piglin"), 5),
						Pair.of(new SinglePoolElement("bastion/mobs/sword_piglin"), 1)
					),
					StructurePool.Projection.RIGID
				)
			);
	}
}
