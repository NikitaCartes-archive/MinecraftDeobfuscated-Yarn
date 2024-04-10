package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class DustColorTransitionParticleEffect extends AbstractDustParticleEffect {
	public static final Vector3f SCULK_BLUE = Vec3d.unpackRgb(3790560).toVector3f();
	public static final DustColorTransitionParticleEffect DEFAULT = new DustColorTransitionParticleEffect(SCULK_BLUE, DustParticleEffect.RED, 1.0F);
	public static final MapCodec<DustColorTransitionParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.VECTOR_3F.fieldOf("from_color").forGetter(effect -> effect.color),
					Codecs.VECTOR_3F.fieldOf("to_color").forGetter(effect -> effect.toColor),
					Codec.FLOAT.fieldOf("scale").forGetter(effect -> effect.scale)
				)
				.apply(instance, DustColorTransitionParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DustColorTransitionParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VECTOR3F,
		effect -> effect.color,
		PacketCodecs.VECTOR3F,
		effect -> effect.toColor,
		PacketCodecs.FLOAT,
		effect -> effect.scale,
		DustColorTransitionParticleEffect::new
	);
	private final Vector3f toColor;

	public DustColorTransitionParticleEffect(Vector3f fromColor, Vector3f toColor, float scale) {
		super(fromColor, scale);
		this.toColor = toColor;
	}

	public Vector3f getFromColor() {
		return this.color;
	}

	public Vector3f getToColor() {
		return this.toColor;
	}

	@Override
	public ParticleType<DustColorTransitionParticleEffect> getType() {
		return ParticleTypes.DUST_COLOR_TRANSITION;
	}
}
