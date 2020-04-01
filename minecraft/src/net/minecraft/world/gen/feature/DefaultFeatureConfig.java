package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class DefaultFeatureConfig implements FeatureConfig {
	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.emptyMap());
	}

	public static <T> DefaultFeatureConfig deserialize(Dynamic<T> dynamic) {
		return DEFAULT;
	}

	public static DefaultFeatureConfig method_26619(Random random) {
		return DEFAULT;
	}
}
