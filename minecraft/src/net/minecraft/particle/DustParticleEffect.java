package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class DustParticleEffect implements ParticleEffect {
	public static final DustParticleEffect DEFAULT = new DustParticleEffect(1.0F, 0.0F, 0.0F, 1.0F);
	public static final Codec<DustParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("r").forGetter(dustParticleEffect -> dustParticleEffect.red),
					Codec.FLOAT.fieldOf("g").forGetter(dustParticleEffect -> dustParticleEffect.green),
					Codec.FLOAT.fieldOf("b").forGetter(dustParticleEffect -> dustParticleEffect.blue),
					Codec.FLOAT.fieldOf("scale").forGetter(dustParticleEffect -> dustParticleEffect.scale)
				)
				.apply(instance, DustParticleEffect::new)
	);
	public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>() {
		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float i = (float)stringReader.readDouble();
			return new DustParticleEffect(f, g, h, i);
		}

		public DustParticleEffect read(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new DustParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}
	};
	private final float red;
	private final float green;
	private final float blue;
	private final float scale;

	public DustParticleEffect(float red, float green, float blue, float scale) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.scale = MathHelper.clamp(scale, 0.01F, 4.0F);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.red);
		buf.writeFloat(this.green);
		buf.writeFloat(this.blue);
		buf.writeFloat(this.scale);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.scale);
	}

	@Override
	public ParticleType<DustParticleEffect> getType() {
		return ParticleTypes.DUST;
	}

	@Environment(EnvType.CLIENT)
	public float getRed() {
		return this.red;
	}

	@Environment(EnvType.CLIENT)
	public float getGreen() {
		return this.green;
	}

	@Environment(EnvType.CLIENT)
	public float getBlue() {
		return this.blue;
	}

	@Environment(EnvType.CLIENT)
	public float getScale() {
		return this.scale;
	}
}
