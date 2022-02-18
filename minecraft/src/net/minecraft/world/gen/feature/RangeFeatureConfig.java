package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeFeatureConfig implements FeatureConfig {
	public static final Codec<RangeFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(HeightProvider.CODEC.fieldOf("height").forGetter(config -> config.heightProvider)).apply(instance, RangeFeatureConfig::new)
	);
	public final HeightProvider heightProvider;

	public RangeFeatureConfig(HeightProvider heightProvider) {
		this.heightProvider = heightProvider;
	}
}
