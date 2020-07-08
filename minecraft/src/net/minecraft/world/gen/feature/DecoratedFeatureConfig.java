package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class DecoratedFeatureConfig implements FeatureConfig {
	public static final Codec<DecoratedFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.fieldOf("feature").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.feature),
					ConfiguredDecorator.CODEC.fieldOf("decorator").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.decorator)
				)
				.apply(instance, DecoratedFeatureConfig::new)
	);
	public final Supplier<ConfiguredFeature<?, ?>> feature;
	public final ConfiguredDecorator<?> decorator;

	public DecoratedFeatureConfig(Supplier<ConfiguredFeature<?, ?>> supplier, ConfiguredDecorator<?> configuredDecorator) {
		this.feature = supplier;
		this.decorator = configuredDecorator;
	}

	public String toString() {
		return String.format(
			"< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(((ConfiguredFeature)this.feature.get()).method_30380()), this.decorator
		);
	}
}
