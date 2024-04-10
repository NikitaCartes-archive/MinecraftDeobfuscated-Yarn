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

public class DustParticleEffect extends AbstractDustParticleEffect {
	public static final Vector3f RED = Vec3d.unpackRgb(16711680).toVector3f();
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(RED, 1.0F);
	public static final MapCodec<DustParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.VECTOR_3F.fieldOf("color").forGetter(effect -> effect.color), Codec.FLOAT.fieldOf("scale").forGetter(effect -> effect.scale)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DustParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VECTOR3F, effect -> effect.color, PacketCodecs.FLOAT, effect -> effect.scale, DustParticleEffect::new
	);

	public DustParticleEffect(Vector3f vector3f, float f) {
		super(vector3f, f);
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}
}
