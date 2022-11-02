package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.registry.Registerable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;

public class BastionData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = poolRegisterable.getRegistryLookup(Registry.STRUCTURE_POOL_KEY);
		RegistryEntry<StructurePool> registryEntry = registryEntryLookup.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"bastion/mobs/piglin",
			new StructurePool(
				registryEntry,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), 1),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), 4),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/crossbow_piglin"), 4),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/mobs/hoglin",
			new StructurePool(
				registryEntry,
				ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("bastion/mobs/hoglin"), 2), Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/blocks/gold",
			new StructurePool(
				registryEntry,
				ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("bastion/blocks/air"), 3), Pair.of(StructurePoolElement.ofSingle("bastion/blocks/gold"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/mobs/piglin_melee",
			new StructurePool(
				registryEntry,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin_always"), 1),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), 5),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
