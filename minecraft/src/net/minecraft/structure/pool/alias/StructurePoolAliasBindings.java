package net.minecraft.structure.pool.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;

public class StructurePoolAliasBindings {
	public static Codec<? extends StructurePoolAliasBinding> registerAndGetDefalt(Registry<Codec<? extends StructurePoolAliasBinding>> registry) {
		Registry.register(registry, "random", RandomStructurePoolAliasBinding.CODEC);
		Registry.register(registry, "random_group", RandomGroupStructurePoolAliasBinding.CODEC);
		return Registry.register(registry, "direct", DirectStructurePoolAliasBinding.CODEC);
	}

	public static void registerPools(Registerable<StructurePool> pools, RegistryEntry<StructurePool> base, List<StructurePoolAliasBinding> aliases) {
		aliases.stream()
			.flatMap(StructurePoolAliasBinding::streamTargets)
			.map(target -> target.getValue().getPath())
			.forEach(
				path -> StructurePools.register(
						pools, path, new StructurePool(base, List.of(Pair.of(StructurePoolElement.ofSingle(path), 1)), StructurePool.Projection.RIGID)
					)
			);
	}
}
