package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class DustColorTransitionParticleEffect extends AbstractDustParticleEffect {
	public static final Vec3d SCULK_BLUE = Vec3d.unpackRgb(3790560);
	public static final DustColorTransitionParticleEffect DEFAULT = new DustColorTransitionParticleEffect(SCULK_BLUE, DustParticleEffect.RED, 1.0F);
	public static final Codec<DustColorTransitionParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vec3d.field_28243.fieldOf("fromColor").forGetter(dustColorTransitionParticleEffect -> dustColorTransitionParticleEffect.color),
					Vec3d.field_28243.fieldOf("toColor").forGetter(dustColorTransitionParticleEffect -> dustColorTransitionParticleEffect.toColor),
					Codec.FLOAT.fieldOf("scale").forGetter(dustColorTransitionParticleEffect -> dustColorTransitionParticleEffect.scale)
				)
				.apply(instance, DustColorTransitionParticleEffect::new)
	);
	public static final ParticleEffect.Factory<DustColorTransitionParticleEffect> FACTORY = new ParticleEffect.Factory<DustColorTransitionParticleEffect>() {
		public DustColorTransitionParticleEffect read(ParticleType<DustColorTransitionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			Vec3d vec3d = AbstractDustParticleEffect.readColor(stringReader);
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			Vec3d vec3d2 = AbstractDustParticleEffect.readColor(stringReader);
			return new DustColorTransitionParticleEffect(vec3d, vec3d2, f);
		}

		public DustColorTransitionParticleEffect read(ParticleType<DustColorTransitionParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new DustColorTransitionParticleEffect(
				new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat()),
				new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat()),
				packetByteBuf.readFloat()
			);
		}
	};
	private final Vec3d toColor;

	public DustColorTransitionParticleEffect(Vec3d fromColor, Vec3d toColor, float scale) {
		super(fromColor, scale);
		this.toColor = toColor;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getFromColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getToColor() {
		return this.toColor;
	}

	@Override
	public void write(PacketByteBuf buf) {
		super.write(buf);
		buf.writeDouble(this.color.x);
		buf.writeDouble(this.color.y);
		buf.writeDouble(this.color.z);
	}

	@Override
	public String asString() {
		return String.format(
			Locale.ROOT,
			"%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f",
			Registry.PARTICLE_TYPE.getId(this.getType()),
			this.color.x,
			this.color.y,
			this.color.z,
			this.scale,
			this.toColor.x,
			this.toColor.y,
			this.toColor.z
		);
	}

	@Override
	public ParticleType<DustColorTransitionParticleEffect> getType() {
		return ParticleTypes.DUST_COLOR_TRANSITION;
	}
}
