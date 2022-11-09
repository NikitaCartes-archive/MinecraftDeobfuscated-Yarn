package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public abstract class AbstractDustParticleEffect implements ParticleEffect {
	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected final Vector3f color;
	protected final float scale;

	public AbstractDustParticleEffect(Vector3f color, float scale) {
		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01F, 4.0F);
	}

	public static Vector3f readColor(StringReader reader) throws CommandSyntaxException {
		reader.expect(' ');
		float f = reader.readFloat();
		reader.expect(' ');
		float g = reader.readFloat();
		reader.expect(' ');
		float h = reader.readFloat();
		return new Vector3f(f, g, h);
	}

	public static Vector3f readColor(PacketByteBuf buf) {
		return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.color.x());
		buf.writeFloat(this.color.y());
		buf.writeFloat(this.color.z());
		buf.writeFloat(this.scale);
	}

	@Override
	public String asString() {
		return String.format(
			Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale
		);
	}

	public Vector3f getColor() {
		return this.color;
	}

	public float getScale() {
		return this.scale;
	}
}
