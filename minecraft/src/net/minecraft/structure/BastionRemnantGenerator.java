package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;

public class BastionRemnantGenerator {
	public static final StructurePool field_25941 = TemplatePools.register(
		new StructurePool(
			new Identifier("bastion/starts"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.method_30435("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1),
				Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1),
				Pair.of(StructurePoolElement.method_30435("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1),
				Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
		BastionUnitsData.init();
		HoglinStableData.init();
		BastionTreasureData.init();
		BastionBridgeData.init();
		BastionData.init();
	}
}
