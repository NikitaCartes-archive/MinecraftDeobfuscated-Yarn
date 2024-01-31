package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

public class DefaultParticleType extends ParticleType<DefaultParticleType> implements ParticleEffect {
	private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>() {
		public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, StringReader stringReader, RegistryWrapper.WrapperLookup wrapperLookup) {
			return (DefaultParticleType)particleType;
		}
	};
	private final Codec<DefaultParticleType> codec = Codec.unit(this::getType);
	private final PacketCodec<RegistryByteBuf, DefaultParticleType> PACKET_CODEC = PacketCodec.unit(this);

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
	public PacketCodec<RegistryByteBuf, DefaultParticleType> getPacketCodec() {
		return this.PACKET_CODEC;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		return Registries.PARTICLE_TYPE.getId(this).toString();
	}
}
