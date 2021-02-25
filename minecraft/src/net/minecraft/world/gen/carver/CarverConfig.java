package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.ProbabilityConfig;

public class CarverConfig extends ProbabilityConfig {
	public static final Codec<CarverConfig> CONFIG_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(carverConfig -> carverConfig.probability),
					CarverDebugConfig.CODEC.optionalFieldOf("debug_settings", CarverDebugConfig.DEFAULT).forGetter(CarverConfig::getDebugConfig)
				)
				.apply(instance, CarverConfig::new)
	);
	private final CarverDebugConfig debugConfig;

	public CarverConfig(float chance, CarverDebugConfig debugConfig) {
		super(chance);
		this.debugConfig = debugConfig;
	}

	public CarverConfig(float f) {
		this(f, CarverDebugConfig.DEFAULT);
	}

	public CarverDebugConfig getDebugConfig() {
		return this.debugConfig;
	}
}
