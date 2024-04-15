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

public class DustParticleEffect extends class_9679 {
	public static final Vector3f RED = Vec3d.unpackRgb(16711680).toVector3f();
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(RED, 1.0F);
	public static final MapCodec<DustParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.VECTOR_3F.fieldOf("color").forGetter(effect -> effect.field_51492), field_51496.fieldOf("scale").forGetter(class_9679::method_59846)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DustParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VECTOR3F, effect -> effect.field_51492, PacketCodecs.FLOAT, class_9679::method_59846, DustParticleEffect::new
	);
	private final Vector3f field_51492;

	public DustParticleEffect(Vector3f vector3f, float f) {
		super(f);
		this.field_51492 = vector3f;
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}

	public Vector3f method_59843() {
		return this.field_51492;
	}
}
