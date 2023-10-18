package net.minecraft.structure.pool.alias;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface StructurePoolAliasLookup {
	StructurePoolAliasLookup EMPTY = pool -> pool;

	RegistryKey<StructurePool> lookup(RegistryKey<StructurePool> pool);

	static StructurePoolAliasLookup create(List<StructurePoolAliasBinding> bindings, BlockPos pos, long seed) {
		if (bindings.isEmpty()) {
			return EMPTY;
		} else {
			Random random = Random.create(seed).nextSplitter().split(pos);
			Builder<RegistryKey<StructurePool>, RegistryKey<StructurePool>> builder = ImmutableMap.builder();
			bindings.forEach(binding -> binding.forEach(random, builder::put));
			Map<RegistryKey<StructurePool>, RegistryKey<StructurePool>> map = builder.build();
			return alias -> (RegistryKey<StructurePool>)Objects.requireNonNull(
					(RegistryKey)map.getOrDefault(alias, alias), () -> "alias " + alias + " was mapped to null value"
				);
		}
	}
}
