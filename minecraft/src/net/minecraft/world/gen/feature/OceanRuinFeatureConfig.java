package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.Util;

public class OceanRuinFeatureConfig implements FeatureConfig {
	public final OceanRuinFeature.BiomeType biomeType;
	public final float largeProbability;
	public final float clusterProbability;

	public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float largeProbability, float clusterProbability) {
		this.biomeType = biomeType;
		this.largeProbability = largeProbability;
		this.clusterProbability = clusterProbability;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("biome_temp"),
					ops.createString(this.biomeType.getName()),
					ops.createString("large_probability"),
					ops.createFloat(this.largeProbability),
					ops.createString("cluster_probability"),
					ops.createFloat(this.clusterProbability)
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

	public static OceanRuinFeatureConfig method_26620(Random random) {
		return new OceanRuinFeatureConfig(Util.method_26715(OceanRuinFeature.BiomeType.class, random), random.nextFloat() / 5.0F, random.nextFloat() / 10.0F);
	}
}
