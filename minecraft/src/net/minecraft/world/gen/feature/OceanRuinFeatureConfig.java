package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OceanRuinFeatureConfig implements FeatureConfig {
	public static final Codec<OceanRuinFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					OceanRuinFeature.BiomeType.field_24990.fieldOf("biome_temp").forGetter(oceanRuinFeatureConfig -> oceanRuinFeatureConfig.biomeType),
					Codec.FLOAT.fieldOf("large_probability").forGetter(oceanRuinFeatureConfig -> oceanRuinFeatureConfig.largeProbability),
					Codec.FLOAT.fieldOf("cluster_probability").forGetter(oceanRuinFeatureConfig -> oceanRuinFeatureConfig.clusterProbability)
				)
				.apply(instance, OceanRuinFeatureConfig::new)
	);
	public final OceanRuinFeature.BiomeType biomeType;
	public final float largeProbability;
	public final float clusterProbability;

	public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float largeProbability, float clusterProbability) {
		this.biomeType = biomeType;
		this.largeProbability = largeProbability;
		this.clusterProbability = clusterProbability;
	}
}
