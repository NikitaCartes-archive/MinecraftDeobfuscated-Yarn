package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public class EntityEffectParticleEffect implements ParticleEffect {
	private final ParticleType<EntityEffectParticleEffect> type;
	private final int color;

	public static MapCodec<EntityEffectParticleEffect> createCodec(ParticleType<EntityEffectParticleEffect> type) {
		return Codecs.ARGB.<EntityEffectParticleEffect>xmap(color -> new EntityEffectParticleEffect(type, color), effect -> effect.color).fieldOf("color");
	}

	public static PacketCodec<? super ByteBuf, EntityEffectParticleEffect> createPacketCodec(ParticleType<EntityEffectParticleEffect> type) {
		return PacketCodecs.INTEGER.xmap(color -> new EntityEffectParticleEffect(type, color), particleEffect -> particleEffect.color);
	}

	private EntityEffectParticleEffect(ParticleType<EntityEffectParticleEffect> type, int color) {
		this.type = type;
		this.color = color;
	}

	@Override
	public ParticleType<EntityEffectParticleEffect> getType() {
		return this.type;
	}

	public float getRed() {
		return (float)ColorHelper.Argb.getRed(this.color) / 255.0F;
	}

	public float getGreen() {
		return (float)ColorHelper.Argb.getGreen(this.color) / 255.0F;
	}

	public float getBlue() {
		return (float)ColorHelper.Argb.getBlue(this.color) / 255.0F;
	}

	public float getAlpha() {
		return (float)ColorHelper.Argb.getAlpha(this.color) / 255.0F;
	}

	public static EntityEffectParticleEffect create(ParticleType<EntityEffectParticleEffect> type, int color) {
		return new EntityEffectParticleEffect(type, color);
	}

	public static EntityEffectParticleEffect create(ParticleType<EntityEffectParticleEffect> type, float r, float g, float b) {
		return create(type, ColorHelper.Argb.fromFloats(1.0F, r, g, b));
	}
}
