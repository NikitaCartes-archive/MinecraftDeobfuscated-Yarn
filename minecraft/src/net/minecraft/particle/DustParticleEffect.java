package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class DustParticleEffect extends AbstractDustParticleEffect {
	public static final Vec3f RED = new Vec3f(Vec3d.unpackRgb(16711680));
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(RED, 1.0F);
	public static final Codec<DustParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vec3f.CODEC.fieldOf("color").forGetter(dustParticleEffect -> dustParticleEffect.color),
					Codec.FLOAT.fieldOf("scale").forGetter(dustParticleEffect -> dustParticleEffect.scale)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>() {
		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			Vec3f vec3f = AbstractDustParticleEffect.readColor(stringReader);
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			return new DustParticleEffect(vec3f, f);
		}

		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new DustParticleEffect(AbstractDustParticleEffect.readColor(packetByteBuf), packetByteBuf.readFloat());
		}
	};

	public DustParticleEffect(Vec3f vec3f, float f) {
		super(vec3f, f);
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}
}
