package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.registry.Registries;

public class ShriekParticleEffect implements ParticleEffect {
	public static final Codec<ShriekParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.INT.fieldOf("delay").forGetter(particleEffect -> particleEffect.delay)).apply(instance, ShriekParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, ShriekParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT, effect -> effect.delay, ShriekParticleEffect::new
	);
	public static final ParticleEffect.Factory<ShriekParticleEffect> FACTORY = new ParticleEffect.Factory<ShriekParticleEffect>() {
		public ShriekParticleEffect read(ParticleType<ShriekParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			int i = stringReader.readInt();
			return new ShriekParticleEffect(i);
		}
	};
	private final int delay;

	public ShriekParticleEffect(int delay) {
		this.delay = delay;
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
