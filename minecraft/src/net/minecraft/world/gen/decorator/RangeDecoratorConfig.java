package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeDecoratorConfig implements DecoratorConfig, FeatureConfig {
	public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(HeightProvider.CODEC.fieldOf("height").forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.heightProvider))
				.apply(instance, RangeDecoratorConfig::new)
	);
	public final HeightProvider heightProvider;

	public RangeDecoratorConfig(HeightProvider heightProvider) {
		this.heightProvider = heightProvider;
	}
}
