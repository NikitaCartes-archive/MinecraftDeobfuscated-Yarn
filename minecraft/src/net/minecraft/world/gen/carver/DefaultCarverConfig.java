package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DefaultCarverConfig implements CarverConfig {
	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.emptyMap());
	}
}
