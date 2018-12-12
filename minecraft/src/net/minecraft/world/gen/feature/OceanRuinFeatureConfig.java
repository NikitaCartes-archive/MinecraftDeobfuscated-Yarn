package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class OceanRuinFeatureConfig implements FeatureConfig {
	public final OceanRuinFeature.BiomeTemperature biomeTemperature;
	public final float largeProbability;
	public final float clusterProbability;

	public OceanRuinFeatureConfig(OceanRuinFeature.BiomeTemperature biomeTemperature, float f, float g) {
		this.biomeTemperature = biomeTemperature;
		this.largeProbability = f;
		this.clusterProbability = g;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("biome_temp"),
					dynamicOps.createString(this.biomeTemperature.getName()),
					dynamicOps.createString("large_probability"),
					dynamicOps.createFloat(this.largeProbability),
					dynamicOps.createString("cluster_probability"),
					dynamicOps.createFloat(this.clusterProbability)
				)
			)
		);
	}

	public static <T> OceanRuinFeatureConfig deserialize(Dynamic<T> dynamic) {
		OceanRuinFeature.BiomeTemperature biomeTemperature = OceanRuinFeature.BiomeTemperature.byName(dynamic.getString("biome_temp", ""));
		float f = dynamic.getFloat("large_probability", 0.0F);
		float g = dynamic.getFloat("cluster_probability", 0.0F);
		return new OceanRuinFeatureConfig(biomeTemperature, f, g);
	}
}
