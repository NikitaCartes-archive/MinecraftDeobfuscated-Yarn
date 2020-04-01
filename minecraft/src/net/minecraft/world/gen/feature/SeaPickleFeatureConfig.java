package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class SeaPickleFeatureConfig implements FeatureConfig {
	public final int count;

	public SeaPickleFeatureConfig(int count) {
		this.count = count;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("count"), ops.createInt(this.count))));
	}

	public static <T> SeaPickleFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.get("count").asInt(0);
		return new SeaPickleFeatureConfig(i);
	}

	public static SeaPickleFeatureConfig method_26606(Random random) {
		return new SeaPickleFeatureConfig(random.nextInt(30));
	}
}
