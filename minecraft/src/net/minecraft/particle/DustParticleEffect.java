package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

public class DustParticleEffect extends AbstractDustParticleEffect {
	public static final int RED = 16711680;
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(16711680, 1.0F);
	public static final MapCodec<DustParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.RGB.fieldOf("color").forGetter(particle -> particle.color), SCALE_CODEC.fieldOf("scale").forGetter(AbstractDustParticleEffect::getScale)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final PacketCodec<RegistryByteBuf, DustParticleEffect> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, particle -> particle.color, PacketCodecs.FLOAT, AbstractDustParticleEffect::getScale, DustParticleEffect::new
	);
	private final int color;

	public DustParticleEffect(int color, float scale) {
		super(scale);
		this.color = color;
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}

	public Vector3f getColor() {
		return ColorHelper.toVector(this.color);
	}
}
