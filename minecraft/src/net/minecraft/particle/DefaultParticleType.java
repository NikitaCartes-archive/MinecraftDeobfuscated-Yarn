package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class DefaultParticleType extends ParticleType<DefaultParticleType> implements ParticleParameters {
	private static final ParticleParameters.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleParameters.Factory<DefaultParticleType>() {
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
	public ParticleType<DefaultParticleType> method_10295() {
		return this;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.method_10221(this).toString();
	}
}
