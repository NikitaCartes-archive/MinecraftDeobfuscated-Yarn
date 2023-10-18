package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;

public class StructurePoolAliasBindings {
	public static Codec<? extends StructurePoolAliasBinding> registerAndGetDefalt(Registry<Codec<? extends StructurePoolAliasBinding>> registry) {
		Registry.register(registry, "random", RandomStructurePoolAliasBinding.CODEC);
		Registry.register(registry, "random_group", RandomGroupStructurePoolAliasBinding.CODEC);
		return Registry.register(registry, "direct", DirectStructurePoolAliasBinding.CODEC);
	}
}
