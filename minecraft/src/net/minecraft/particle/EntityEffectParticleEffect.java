package net.minecraft.particle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class EntityEffectParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<EntityEffectParticleEffect> PARAMETERS_FACTORY = (type, reader, registryLookup) -> new EntityEffectParticleEffect(
			type, reader.readInt()
		);
	private final ParticleType<? extends EntityEffectParticleEffect> type;
	private final int color;

	public static Codec<EntityEffectParticleEffect> createCodec(ParticleType<EntityEffectParticleEffect> type) {
		return Codec.INT.xmap(color -> new EntityEffectParticleEffect(type, color), effect -> effect.color);
	}

	public static PacketCodec<? super ByteBuf, EntityEffectParticleEffect> createPacketCodec(ParticleType<EntityEffectParticleEffect> type) {
		return PacketCodecs.INTEGER.xmap(color -> new EntityEffectParticleEffect(type, color), particleEffect -> particleEffect.color);
	}

	private EntityEffectParticleEffect(ParticleType<? extends EntityEffectParticleEffect> type, int color) {
		this.type = type;
		this.color = color;
	}

	@Override
	public ParticleType<?> getType() {
		return this.type;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		return String.format(Locale.ROOT, "%s 0x%x", Registries.PARTICLE_TYPE.getId(this.getType()), this.color);
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

	public static EntityEffectParticleEffect create(ParticleType<? extends EntityEffectParticleEffect> type, int color) {
		return new EntityEffectParticleEffect(type, color);
	}

	public static EntityEffectParticleEffect create(ParticleType<? extends EntityEffectParticleEffect> type, float r, float g, float b) {
		return create(type, ColorHelper.Argb.getArgb(toInt(r), toInt(g), toInt(b)));
	}

	private static int toInt(float value) {
		return MathHelper.floor(value * 255.0F);
	}
}
