package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;

public class DecoratedFeatureConfig implements FeatureConfig {
	public final ConfiguredFeature<?, ?> feature;
	public final ConfiguredDecorator<?> decorator;

	public DecoratedFeatureConfig(ConfiguredFeature<?, ?> configuredFeature, ConfiguredDecorator<?> configuredDecorator) {
		this.feature = configuredFeature;
		this.decorator = configuredDecorator;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("feature"), this.feature.serialize(ops).getValue(), ops.createString("decorator"), this.decorator.serialize(ops).getValue()
				)
			)
		);
	}

	public String toString() {
		return String.format(
			"< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this.feature.feature), Registry.DECORATOR.getId(this.decorator.decorator)
		);
	}

	public static <T> DecoratedFeatureConfig deserialize(Dynamic<T> dynamic) {
		ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
		ConfiguredDecorator<?> configuredDecorator = ConfiguredDecorator.deserialize(dynamic.get("decorator").orElseEmptyMap());
		return new DecoratedFeatureConfig(configuredFeature, configuredDecorator);
	}

	public static DecoratedFeatureConfig method_26608(Random random) {
		return new DecoratedFeatureConfig(Registry.FEATURE.getRandom(random).method_26588(random), Registry.DECORATOR.getRandom(random).method_26672(random));
	}
}
