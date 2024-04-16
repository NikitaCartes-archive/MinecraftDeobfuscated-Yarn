package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractDustParticleEffect implements ParticleEffect {
	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected static final Codec<Float> SCALE_CODEC = Codec.FLOAT
		.validate(scale -> scale >= 0.01F && scale <= 4.0F ? DataResult.success(scale) : DataResult.error(() -> "Value must be within range [0.01;4.0]: " + scale));
	private final float scale;

	public AbstractDustParticleEffect(float scale) {
		this.scale = MathHelper.clamp(scale, 0.01F, 4.0F);
	}

	public float getScale() {
		return this.scale;
	}
}
