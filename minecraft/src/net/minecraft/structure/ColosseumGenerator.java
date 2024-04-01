package net.minecraft.structure;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;

public class ColosseumGenerator {
	public static final RegistryKey<StructurePool> STARTS_KEY = StructurePools.of("colosseum/starts");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		poolRegisterable.register(
			STARTS_KEY,
			new StructurePool(
				poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL).getOrThrow(StructurePools.EMPTY),
				List.of(
					Pair.of(
						StructurePoolElement.ofProcessedSingle(
							"colosseum/treasure/big_air_full", poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST).getOrThrow(StructureProcessorLists.COLOSSEUM_VEINS)
						),
						1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		ColosseumTreasureData.bootstrap(poolRegisterable);
		ColosseumMobsData.bootstrap(poolRegisterable);
	}
}
