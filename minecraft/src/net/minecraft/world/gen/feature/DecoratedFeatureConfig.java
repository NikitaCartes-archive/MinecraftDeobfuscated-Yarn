package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class DecoratedFeatureConfig implements FeatureConfig {
	public static final Codec<DecoratedFeatureConfig> field_24880 = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.field_24833.fieldOf("feature").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.feature),
					ConfiguredDecorator.field_24981.fieldOf("decorator").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.decorator)
				)
				.apply(instance, DecoratedFeatureConfig::new)
	);
	public final ConfiguredFeature<?, ?> feature;
	public final ConfiguredDecorator<?> decorator;

	public DecoratedFeatureConfig(ConfiguredFeature<?, ?> configuredFeature, ConfiguredDecorator<?> configuredDecorator) {
		this.feature = configuredFeature;
		this.decorator = configuredDecorator;
	}

	public String toString() {
		return String.format(
			"< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this.feature.feature), Registry.DECORATOR.getId(this.decorator.decorator)
		);
	}
}
