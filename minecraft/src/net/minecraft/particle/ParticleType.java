package net.minecraft.particle;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class ParticleType<T extends ParticleEffect> {
	private final boolean alwaysShow;
	private final ParticleEffect.Factory<T> parametersFactory;

	/**
	 * @param alwaysShow whether this particle type should appear regardless of {@linkplain net.minecraft.client.options.GameOptions#particles particle mode}
	 */
	protected ParticleType(boolean alwaysShow, ParticleEffect.Factory<T> parametersFactory) {
		this.alwaysShow = alwaysShow;
		this.parametersFactory = parametersFactory;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAlwaysSpawn() {
		return this.alwaysShow;
	}

	public ParticleEffect.Factory<T> getParametersFactory() {
		return this.parametersFactory;
	}

	public abstract Codec<T> getCodec();
}
