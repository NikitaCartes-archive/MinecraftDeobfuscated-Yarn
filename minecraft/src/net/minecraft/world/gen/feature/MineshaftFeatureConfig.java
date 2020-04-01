package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.Util;

public class MineshaftFeatureConfig implements FeatureConfig {
	public final double probability;
	public final MineshaftFeature.Type type;

	public MineshaftFeatureConfig(double probability, MineshaftFeature.Type type) {
		this.probability = probability;
		this.type = type;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability), ops.createString("type"), ops.createString(this.type.getName()))
			)
		);
	}

	public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		MineshaftFeature.Type type = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
		return new MineshaftFeatureConfig((double)f, type);
	}

	public static MineshaftFeatureConfig method_26616(Random random) {
		return new MineshaftFeatureConfig((double)(random.nextFloat() / 2.0F), Util.method_26715(MineshaftFeature.Type.class, random));
	}
}
