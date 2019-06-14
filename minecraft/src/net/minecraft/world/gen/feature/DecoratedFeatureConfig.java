package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class DecoratedFeatureConfig implements FeatureConfig {
	public final ConfiguredFeature<?> feature;
	public final ConfiguredDecorator<?> field_13398;

	public DecoratedFeatureConfig(ConfiguredFeature<?> configuredFeature, ConfiguredDecorator<?> configuredDecorator) {
		this.feature = configuredFeature;
		this.field_13398 = configuredDecorator;
	}

	public <F extends FeatureConfig, D extends DecoratorConfig> DecoratedFeatureConfig(
		Feature<F> feature, F featureConfig, Decorator<D> decorator, D decoratorConfig
	) {
		this(new ConfiguredFeature<>(feature, featureConfig), new ConfiguredDecorator<>(decorator, decoratorConfig));
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("feature"),
					this.feature.serialize(dynamicOps).getValue(),
					dynamicOps.createString("decorator"),
					this.field_13398.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public String toString() {
		return String.format(
			"< %s [%s | %s] >",
			this.getClass().getSimpleName(),
			Registry.FEATURE.getId(this.feature.field_13376),
			Registry.DECORATOR.getId(this.field_13398.field_14115)
		);
	}

	public static <T> DecoratedFeatureConfig deserialize(Dynamic<T> dynamic) {
		ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
		ConfiguredDecorator<?> configuredDecorator = ConfiguredDecorator.deserialize(dynamic.get("decorator").orElseEmptyMap());
		return new DecoratedFeatureConfig(configuredFeature, configuredDecorator);
	}
}
