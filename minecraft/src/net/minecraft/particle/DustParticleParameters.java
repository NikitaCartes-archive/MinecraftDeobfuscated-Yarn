package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class DustParticleParameters implements ParticleParameters {
	public static final DustParticleParameters RED = new DustParticleParameters(1.0F, 0.0F, 0.0F, 1.0F);
	public static final ParticleParameters.Factory<DustParticleParameters> PARAMETERS_FACTORY = new ParticleParameters.Factory<DustParticleParameters>() {
		public DustParticleParameters read(ParticleType<DustParticleParameters> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float i = (float)stringReader.readDouble();
			return new DustParticleParameters(f, g, h, i);
		}

		public DustParticleParameters read(ParticleType<DustParticleParameters> particleType, PacketByteBuf packetByteBuf) {
			return new DustParticleParameters(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
		}
	};
	private final float red;
	private final float green;
	private final float blue;
	private final float alpha;

	public DustParticleParameters(float f, float g, float h, float i) {
		this.red = f;
		this.green = g;
		this.blue = h;
		this.alpha = MathHelper.clamp(i, 0.01F, 4.0F);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeFloat(this.red);
		packetByteBuf.writeFloat(this.green);
		packetByteBuf.writeFloat(this.blue);
		packetByteBuf.writeFloat(this.alpha);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.alpha);
	}

	@Override
	public ParticleType<DustParticleParameters> getType() {
		return ParticleTypes.field_11212;
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
	public float getAlpha() {
		return this.alpha;
	}
}
