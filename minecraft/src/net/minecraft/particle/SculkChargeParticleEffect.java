package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect {
	public static final MapCodec<SculkChargeParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.FLOAT.fieldOf("roll").forGetter(particleEffect -> particleEffect.roll)).apply(instance, SculkChargeParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, SculkChargeParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT, effect -> effect.roll, SculkChargeParticleEffect::new
	);

	@Override
	public ParticleType<SculkChargeParticleEffect> getType() {
		return ParticleTypes.SCULK_CHARGE;
	}
}
