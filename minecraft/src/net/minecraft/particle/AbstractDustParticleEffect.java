package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public abstract class AbstractDustParticleEffect implements ParticleEffect {
	protected final Vec3d color;
	protected final float scale;

	public AbstractDustParticleEffect(Vec3d color, float scale) {
		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01F, 4.0F);
	}

	public static Vec3d readColor(StringReader stringReader) throws CommandSyntaxException {
		stringReader.expect(' ');
		float f = (float)stringReader.readDouble();
		stringReader.expect(' ');
		float g = (float)stringReader.readDouble();
		stringReader.expect(' ');
		float h = (float)stringReader.readDouble();
		return new Vec3d((double)f, (double)g, (double)h);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.color.x);
		buf.writeDouble(this.color.y);
		buf.writeDouble(this.color.z);
		buf.writeFloat(this.scale);
	}

	@Override
	public String asString() {
		return String.format(
			Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.color.x, this.color.y, this.color.z, this.scale
		);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	public float getScale() {
		return this.scale;
	}
}
