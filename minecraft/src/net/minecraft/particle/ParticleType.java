package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class ParticleType<T extends ParticleEffect> {
	private final boolean alwaysShow;

	protected ParticleType(boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
	}

	public boolean shouldAlwaysSpawn() {
		return this.alwaysShow;
	}

	public abstract MapCodec<T> getCodec();

	public abstract PacketCodec<? super RegistryByteBuf, T> getPacketCodec();
}
