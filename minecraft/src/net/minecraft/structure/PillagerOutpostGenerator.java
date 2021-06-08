package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class PillagerOutpostGenerator {
	public static final StructurePool STRUCTURE_POOLS = StructurePools.register(
		new StructurePool(
			new Identifier("pillager_outpost/base_plates"),
			new Identifier("empty"),
			ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/base_plate"), 1)),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/towers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(
						StructurePoolElement.ofList(
							ImmutableList.of(
								StructurePoolElement.ofLegacySingle("pillager_outpost/watchtower"),
								StructurePoolElement.ofProcessedLegacySingle("pillager_outpost/watchtower_overgrown", StructureProcessorLists.OUTPOST_ROT)
							)
						),
						1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/feature_plates"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_plate"), 1)),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("pillager_outpost/features"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_cage1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_cage2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_logs"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_tent1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_tent2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_targets"), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
