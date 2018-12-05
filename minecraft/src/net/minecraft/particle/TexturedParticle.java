package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class TexturedParticle extends ParticleType<TexturedParticle> implements Particle {
	private static final Particle.class_2395<TexturedParticle> field_11259 = new Particle.class_2395<TexturedParticle>() {
		public TexturedParticle method_10307(ParticleType<TexturedParticle> particleType, StringReader stringReader) throws CommandSyntaxException {
			return (TexturedParticle)particleType;
		}

		public TexturedParticle method_10306(ParticleType<TexturedParticle> particleType, PacketByteBuf packetByteBuf) {
			return (TexturedParticle)particleType;
		}
	};

	protected TexturedParticle(boolean bl) {
		super(bl, field_11259);
	}

	@Override
	public ParticleType<TexturedParticle> getParticleType() {
		return this;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this).toString();
	}
}
