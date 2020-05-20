package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ProbabilityConfig implements CarverConfig, FeatureConfig {
	public static final Codec<ProbabilityConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("probability").withDefault(0.0F).forGetter(probabilityConfig -> probabilityConfig.probability))
				.apply(instance, ProbabilityConfig::new)
	);
	public final float probability;

	public ProbabilityConfig(float probability) {
		this.probability = probability;
	}
}
