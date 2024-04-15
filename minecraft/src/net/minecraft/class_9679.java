package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;

public abstract class class_9679 implements ParticleEffect {
	public static final float field_51494 = 0.01F;
	public static final float field_51495 = 4.0F;
	protected static final Codec<Float> field_51496 = Codec.FLOAT
		.validate(
			float_ -> float_ >= 0.01F && float_ <= 4.0F ? DataResult.success(float_) : DataResult.error(() -> "Value must be within range [0.01;4.0]: " + float_)
		);
	private final float field_51493;

	public class_9679(float f) {
		this.field_51493 = MathHelper.clamp(f, 0.01F, 4.0F);
	}

	public float method_59846() {
		return this.field_51493;
	}
}
