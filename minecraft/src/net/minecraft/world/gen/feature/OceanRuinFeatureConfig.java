package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class OceanRuinFeatureConfig implements FeatureConfig {
	public final OceanRuinFeature.BiomeType field_13709;
	public final float largeProbability;
	public final float clusterProbability;

	public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float f, float g) {
		this.field_13709 = biomeType;
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
					dynamicOps.createString(this.field_13709.getName()),
					dynamicOps.createString("large_probability"),
					dynamicOps.createFloat(this.largeProbability),
					dynamicOps.createString("cluster_probability"),
					dynamicOps.createFloat(this.clusterProbability)
				)
			)
		);
	}

	public static <T> OceanRuinFeatureConfig deserialize(Dynamic<T> dynamic) {
		OceanRuinFeature.BiomeType biomeType = OceanRuinFeature.BiomeType.byName(dynamic.get("biome_temp").asString(""));
		float f = dynamic.get("large_probability").asFloat(0.0F);
		float g = dynamic.get("cluster_probability").asFloat(0.0F);
		return new OceanRuinFeatureConfig(biomeType, f, g);
	}
}
