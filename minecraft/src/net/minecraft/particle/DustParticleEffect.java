package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class DustParticleEffect extends AbstractDustParticleEffect {
	public static final Vec3d RED = Vec3d.unpackRgb(16711680);
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(RED, 1.0F);
	public static final Codec<DustParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vec3d.field_28243.fieldOf("color").forGetter(dustParticleEffect -> dustParticleEffect.color),
					Codec.FLOAT.fieldOf("scale").forGetter(dustParticleEffect -> dustParticleEffect.scale)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>() {
		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			Vec3d vec3d = AbstractDustParticleEffect.readColor(stringReader);
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			return new DustParticleEffect(vec3d, f);
		}

		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new DustParticleEffect(
				new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat()), packetByteBuf.readFloat()
			);
		}
	};

	public DustParticleEffect(Vec3d vec3d, float f) {
		super(vec3d, f);
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}
}
