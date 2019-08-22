package net.minecraft.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ParticleType<T extends ParticleEffect> {
	private final boolean shouldAlwaysSpawn;
	private final ParticleEffect.Factory<T> parametersFactory;

	protected ParticleType(boolean bl, ParticleEffect.Factory<T> factory) {
		this.shouldAlwaysSpawn = bl;
		this.parametersFactory = factory;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAlwaysSpawn() {
		return this.shouldAlwaysSpawn;
	}

	public ParticleEffect.Factory<T> getParametersFactory() {
		return this.parametersFactory;
	}
}
