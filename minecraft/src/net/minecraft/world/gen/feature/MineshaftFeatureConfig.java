package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class MineshaftFeatureConfig implements FeatureConfig {
	public final double probability;
	public final MineshaftFeature.Type field_13694;

	public MineshaftFeatureConfig(double d, MineshaftFeature.Type type) {
		this.probability = d;
		this.field_13694 = type;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("probability"),
					dynamicOps.createDouble(this.probability),
					dynamicOps.createString("type"),
					dynamicOps.createString(this.field_13694.getName())
				)
			)
		);
	}

	public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		MineshaftFeature.Type type = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
		return new MineshaftFeatureConfig((double)f, type);
	}
}
