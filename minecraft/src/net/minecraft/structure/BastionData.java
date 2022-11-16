package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;

public class BastionData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
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
