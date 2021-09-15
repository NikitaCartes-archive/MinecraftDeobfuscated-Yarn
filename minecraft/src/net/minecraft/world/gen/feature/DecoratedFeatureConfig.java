package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class DecoratedFeatureConfig implements FeatureConfig {
	public static final Codec<DecoratedFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.feature),
					ConfiguredDecorator.CODEC.fieldOf("decorator").forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.decorator)
				)
				.apply(instance, DecoratedFeatureConfig::new)
	);
	public final Supplier<ConfiguredFeature<?, ?>> feature;
	public final ConfiguredDecorator<?> decorator;

	public DecoratedFeatureConfig(Supplier<ConfiguredFeature<?, ?>> feature, ConfiguredDecorator<?> decorator) {
		this.feature = feature;
		this.decorator = decorator;
	}

	public String toString() {
		return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), this.feature.get(), this.decorator);
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return ((ConfiguredFeature)this.feature.get()).getDecoratedFeatures();
	}
}
