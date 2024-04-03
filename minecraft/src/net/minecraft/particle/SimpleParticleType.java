package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

/**
 * A particle type representing a particle with no additional parameters.
 * 
 * <p>Because no additional parameters can be provided, this particle type
 * itself implements {@link ParticleEffect} and can be passed to methods
 * which accept particle parameters.
 */
public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleEffect {
	private static final ParticleEffect.Factory<SimpleParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<SimpleParticleType>() {
		public SimpleParticleType read(ParticleType<SimpleParticleType> particleType, StringReader stringReader, RegistryWrapper.WrapperLookup wrapperLookup) {
			return (SimpleParticleType)particleType;
		}
	};
	private final MapCodec<SimpleParticleType> codec = MapCodec.unit(this::getType);
	private final PacketCodec<RegistryByteBuf, SimpleParticleType> PACKET_CODEC = PacketCodec.unit(this);

	protected SimpleParticleType(boolean alwaysShow) {
		super(alwaysShow, PARAMETER_FACTORY);
	}

	public SimpleParticleType getType() {
		return this;
	}

	@Override
	public MapCodec<SimpleParticleType> getCodec() {
		return this.codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, SimpleParticleType> getPacketCodec() {
		return this.PACKET_CODEC;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		return Registries.PARTICLE_TYPE.getId(this).toString();
	}
}
