package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect {
	public static final Codec<SculkChargeParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("roll").forGetter(particleEffect -> particleEffect.roll)).apply(instance, SculkChargeParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, SculkChargeParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT, effect -> effect.roll, SculkChargeParticleEffect::new
	);
	public static final ParticleEffect.Factory<SculkChargeParticleEffect> FACTORY = new ParticleEffect.Factory<SculkChargeParticleEffect>() {
		public SculkChargeParticleEffect read(
			ParticleType<SculkChargeParticleEffect> particleType, StringReader stringReader, RegistryWrapper.WrapperLookup wrapperLookup
		) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			return new SculkChargeParticleEffect(f);
		}
	};

	@Override
	public ParticleType<SculkChargeParticleEffect> getType() {
		return ParticleTypes.SCULK_CHARGE;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		return String.format(Locale.ROOT, "%s %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.roll);
	}
}
