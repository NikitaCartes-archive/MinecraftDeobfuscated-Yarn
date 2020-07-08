package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DecoratedDecoratorConfig implements DecoratorConfig {
	public static final Codec<DecoratedDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredDecorator.CODEC.fieldOf("outer").forGetter(DecoratedDecoratorConfig::method_30455),
					ConfiguredDecorator.CODEC.fieldOf("inner").forGetter(DecoratedDecoratorConfig::method_30457)
				)
				.apply(instance, DecoratedDecoratorConfig::new)
	);
	private final ConfiguredDecorator<?> field_25855;
	private final ConfiguredDecorator<?> field_25856;

	public DecoratedDecoratorConfig(ConfiguredDecorator<?> configuredDecorator, ConfiguredDecorator<?> configuredDecorator2) {
		this.field_25855 = configuredDecorator;
		this.field_25856 = configuredDecorator2;
	}

	public ConfiguredDecorator<?> method_30455() {
		return this.field_25855;
	}

	public ConfiguredDecorator<?> method_30457() {
		return this.field_25856;
	}
}
