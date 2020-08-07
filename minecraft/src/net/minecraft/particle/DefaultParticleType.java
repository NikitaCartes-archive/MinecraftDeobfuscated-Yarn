package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class DefaultParticleType extends ParticleType<DefaultParticleType> implements ParticleEffect {
	private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>() {
		public DefaultParticleType method_10307(ParticleType<DefaultParticleType> particleType, StringReader stringReader) throws CommandSyntaxException {
			return (DefaultParticleType)particleType;
		}

		public DefaultParticleType method_10306(ParticleType<DefaultParticleType> particleType, PacketByteBuf packetByteBuf) {
			return (DefaultParticleType)particleType;
		}
	};
	private final Codec<DefaultParticleType> field_25127 = Codec.unit(this::method_29140);

	protected DefaultParticleType(boolean bl) {
		super(bl, PARAMETER_FACTORY);
	}

	public DefaultParticleType method_29140() {
		return this;
	}

	@Override
	public Codec<DefaultParticleType> method_29138() {
		return this.field_25127;
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this).toString();
	}
}
