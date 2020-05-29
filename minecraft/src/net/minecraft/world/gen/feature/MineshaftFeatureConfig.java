package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MineshaftFeatureConfig implements FeatureConfig {
	public static final Codec<MineshaftFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("probability").forGetter(mineshaftFeatureConfig -> mineshaftFeatureConfig.probability),
					MineshaftFeature.Type.field_24839.fieldOf("type").forGetter(mineshaftFeatureConfig -> mineshaftFeatureConfig.type)
				)
				.apply(instance, MineshaftFeatureConfig::new)
	);
	public final double probability;
	public final MineshaftFeature.Type type;

	public MineshaftFeatureConfig(double probability, MineshaftFeature.Type type) {
		this.probability = probability;
		this.type = type;
	}
}
