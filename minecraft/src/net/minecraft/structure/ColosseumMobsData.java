package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;

public class ColosseumMobsData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntry<StructurePool> registryEntry = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL).getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"colosseum/mobs/toxifin",
			new StructurePool(registryEntry, ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("colosseum/mobs/toxifin"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/mobs/plaguewhale",
			new StructurePool(registryEntry, ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("colosseum/mobs/plaguewhale"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"colosseum/mobs/mega_spud",
			new StructurePool(registryEntry, ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("colosseum/mobs/mega_spud"), 1)), StructurePool.Projection.RIGID)
		);
	}
}
