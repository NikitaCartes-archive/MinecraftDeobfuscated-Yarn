package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig implements FeatureConfig {
	public final double probability;
	public final MineshaftFeature.Type type;

	public MineshaftFeatureConfig(double d, MineshaftFeature.Type type) {
		this.probability = d;
		this.type = type;
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
					dynamicOps.createString(this.type.getName())
				)
			)
		);
	}

	public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.getFloat("probability", 0.0F);
		MineshaftFeature.Type type = MineshaftFeature.Type.byName(dynamic.getString("type", ""));
		return new MineshaftFeatureConfig((double)f, type);
	}
}
