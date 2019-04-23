package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;
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

	protected DefaultParticleType(boolean bl) {
		super(bl, PARAMETER_FACTORY);
	}

	@Override
	public ParticleType<DefaultParticleType> getType() {
		return this;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this).toString();
	}
}
