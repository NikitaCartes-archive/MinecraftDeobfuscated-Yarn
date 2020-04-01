package net.minecraft.particle;

import java.util.Random;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ParticleType<T extends ParticleEffect> {
	private final boolean shouldAlwaysSpawn;
	private final ParticleEffect.Factory<T> parametersFactory;
	private final BiFunction<Random, ParticleType<T>, T> field_23636;

	public ParticleType(boolean shouldAlwaysShow, ParticleEffect.Factory<T> parametersFactory, BiFunction<Random, ParticleType<T>, T> biFunction) {
		this.shouldAlwaysSpawn = shouldAlwaysShow;
		this.parametersFactory = parametersFactory;
		this.field_23636 = biFunction;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAlwaysSpawn() {
		return this.shouldAlwaysSpawn;
	}

	public ParticleEffect.Factory<T> getParametersFactory() {
		return this.parametersFactory;
	}

	public T method_26703(Random random) {
		return (T)this.field_23636.apply(random, this);
	}
}
