package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public class ShriekParticleEffect implements ParticleEffect {
	public static final Codec<ShriekParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.INT.fieldOf("delay").forGetter(particleEffect -> particleEffect.delay)).apply(instance, ShriekParticleEffect::new)
	);
	public static final ParticleEffect.Factory<ShriekParticleEffect> FACTORY = new ParticleEffect.Factory<ShriekParticleEffect>() {
		public ShriekParticleEffect read(ParticleType<ShriekParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			int i = stringReader.readInt();
			return new ShriekParticleEffect(i);
		}

		public ShriekParticleEffect read(ParticleType<ShriekParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new ShriekParticleEffect(packetByteBuf.readVarInt());
		}
	};
	private final int delay;

	public ShriekParticleEffect(int delay) {
		this.delay = delay;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.delay);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %d", Registries.PARTICLE_TYPE.getId(this.getType()), this.delay);
	}

	@Override
	public ParticleType<ShriekParticleEffect> getType() {
		return ParticleTypes.SHRIEK;
	}

	public int getDelay() {
		return this.delay;
	}
}
