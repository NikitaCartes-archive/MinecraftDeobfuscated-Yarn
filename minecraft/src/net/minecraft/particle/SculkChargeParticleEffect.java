package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect {
	public static final Codec<SculkChargeParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("roll").forGetter(particleEffect -> particleEffect.roll)).apply(instance, SculkChargeParticleEffect::new)
	);
	public static final ParticleEffect.Factory<SculkChargeParticleEffect> FACTORY = new ParticleEffect.Factory<SculkChargeParticleEffect>() {
		public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			return new SculkChargeParticleEffect(f);
		}

		public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new SculkChargeParticleEffect(packetByteBuf.readFloat());
		}
	};

	@Override
	public ParticleType<SculkChargeParticleEffect> getType() {
		return ParticleTypes.SCULK_CHARGE;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.roll);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.roll);
	}
}
