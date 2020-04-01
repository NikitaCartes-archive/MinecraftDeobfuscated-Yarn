package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface CarverConfig {
	DefaultCarverConfig DEFAULT = new DefaultCarverConfig();

	<T> Dynamic<T> serialize(DynamicOps<T> ops);
}
