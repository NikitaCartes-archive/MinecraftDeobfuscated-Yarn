package net.minecraft.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ParticleType<T extends ParticleParameters> {
	private final boolean shouldAlwaysSpawn;
	private final ParticleParameters.Factory<T> parametersFactory;

	protected ParticleType(boolean bl, ParticleParameters.Factory<T> factory) {
		this.shouldAlwaysSpawn = bl;
		this.parametersFactory = factory;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAlwaysSpawn() {
		return this.shouldAlwaysSpawn;
	}

	public ParticleParameters.Factory<T> getParametersFactory() {
		return this.parametersFactory;
	}
}
