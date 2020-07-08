package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.Identifier;

public class PillagerOutpostGenerator {
	public static final StructurePool field_26252 = TemplatePools.register(
		new StructurePool(
			new Identifier("pillager_outpost/base_plates"),
			new Identifier("empty"),
			ImmutableList.of(Pair.of(StructurePoolElement.method_30425("pillager_outpost/base_plate"), 1)),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		TemplatePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/towers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(
						StructurePoolElement.method_30429(
							ImmutableList.of(
								StructurePoolElement.method_30425("pillager_outpost/watchtower"),
								StructurePoolElement.method_30426("pillager_outpost/watchtower_overgrown", ImmutableList.of(new BlockRotStructureProcessor(0.05F)))
							)
						),
						1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/feature_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_plate"), 1)),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/features"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_cage1"), 1),
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_cage2"), 1),
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_logs"), 1),
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_tent1"), 1),
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_tent2"), 1),
					Pair.of(StructurePoolElement.method_30425("pillager_outpost/feature_targets"), 1),
					Pair.of(StructurePoolElement.method_30438(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
