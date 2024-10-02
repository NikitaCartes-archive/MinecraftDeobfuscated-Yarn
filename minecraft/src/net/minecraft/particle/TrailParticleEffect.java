package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public record TrailParticleEffect(Vec3d target, int color) implements ParticleEffect {
	public static final MapCodec<TrailParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Vec3d.CODEC.fieldOf("target").forGetter(TrailParticleEffect::target), Codec.INT.fieldOf("color").forGetter(TrailParticleEffect::color)
				)
				.apply(instance, TrailParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, TrailParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		Vec3d.PACKET_CODEC, TrailParticleEffect::target, PacketCodecs.INTEGER, TrailParticleEffect::color, TrailParticleEffect::new
	);

	@Override
	public ParticleType<TrailParticleEffect> getType() {
		return ParticleTypes.TRAIL;
	}
}
