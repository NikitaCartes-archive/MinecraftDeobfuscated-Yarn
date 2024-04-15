package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_9679;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class DustColorTransitionParticleEffect extends class_9679 {
	public static final Vector3f SCULK_BLUE = Vec3d.unpackRgb(3790560).toVector3f();
	public static final DustColorTransitionParticleEffect DEFAULT = new DustColorTransitionParticleEffect(SCULK_BLUE, DustParticleEffect.RED, 1.0F);
	public static final MapCodec<DustColorTransitionParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.VECTOR_3F.fieldOf("from_color").forGetter(effect -> effect.field_51491),
					Codecs.VECTOR_3F.fieldOf("to_color").forGetter(effect -> effect.toColor),
					field_51496.fieldOf("scale").forGetter(class_9679::method_59846)
				)
				.apply(instance, DustColorTransitionParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DustColorTransitionParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VECTOR3F,
		effect -> effect.field_51491,
		PacketCodecs.VECTOR3F,
		effect -> effect.toColor,
		PacketCodecs.FLOAT,
		class_9679::method_59846,
		DustColorTransitionParticleEffect::new
	);
	private final Vector3f field_51491;
	private final Vector3f toColor;

	public DustColorTransitionParticleEffect(Vector3f vector3f, Vector3f toColor, float scale) {
		super(scale);
		this.field_51491 = vector3f;
		this.toColor = toColor;
	}

	public Vector3f getFromColor() {
		return this.field_51491;
	}

	public Vector3f getToColor() {
		return this.toColor;
	}

	@Override
	public ParticleType<DustColorTransitionParticleEffect> getType() {
		return ParticleTypes.DUST_COLOR_TRANSITION;
	}
}
