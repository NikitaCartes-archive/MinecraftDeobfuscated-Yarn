package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
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

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
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
