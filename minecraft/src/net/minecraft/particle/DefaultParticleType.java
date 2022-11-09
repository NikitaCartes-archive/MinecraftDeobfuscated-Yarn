package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public class DefaultParticleType extends ParticleType<DefaultParticleType> implements ParticleEffect {
	private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>() {
		public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, StringReader stringReader) {
			return (DefaultParticleType)particleType;
		}

		public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, PacketByteBuf packetByteBuf) {
			return (DefaultParticleType)particleType;
		}
	};
	private final Codec<DefaultParticleType> codec = Codec.unit(this::getType);

	protected DefaultParticleType(boolean alwaysShow) {
		super(alwaysShow, PARAMETER_FACTORY);
	}

	public DefaultParticleType getType() {
		return this;
	}

	@Override
	public Codec<DefaultParticleType> getCodec() {
		return this.codec;
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public String asString() {
		return Registries.PARTICLE_TYPE.getId(this).toString();
	}
}
