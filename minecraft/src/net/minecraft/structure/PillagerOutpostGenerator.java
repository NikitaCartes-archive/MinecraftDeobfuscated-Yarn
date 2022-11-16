package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;

public class PillagerOutpostGenerator {
	public static final RegistryKey<StructurePool> STRUCTURE_POOLS = StructurePools.of("pillager_outpost/base_plates");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.OUTPOST_ROT);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry2 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		poolRegisterable.register(
			STRUCTURE_POOLS,
			new StructurePool(
				registryEntry2, ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/base_plate"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"pillager_outpost/towers",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(
						StructurePoolElement.ofList(
							ImmutableList.of(
								StructurePoolElement.ofLegacySingle("pillager_outpost/watchtower"),
								StructurePoolElement.ofProcessedLegacySingle("pillager_outpost/watchtower_overgrown", registryEntry)
							)
						),
						1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"pillager_outpost/feature_plates",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_plate"), 1)),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			poolRegisterable,
			"pillager_outpost/features",
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_cage1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_cage2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("pillager_outpost/feature_cage_with_allays"), 1),
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
