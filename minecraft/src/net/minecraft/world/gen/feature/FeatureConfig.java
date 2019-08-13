package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface FeatureConfig {
	DefaultFeatureConfig DEFAULT = new DefaultFeatureConfig();

	<T> Dynamic<T> serialize(DynamicOps<T> dynamicOps);
}
