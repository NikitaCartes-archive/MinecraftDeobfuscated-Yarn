package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record SlideConfig(double target, int size, int offset) {
	public static final Codec<SlideConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("target").forGetter(slideConfig -> slideConfig.target),
					Codecs.NONNEGATIVE_INT.fieldOf("size").forGetter(slideConfig -> slideConfig.size),
					Codec.INT.fieldOf("offset").forGetter(slideConfig -> slideConfig.offset)
				)
				.apply(instance, SlideConfig::new)
	);

	public double method_38414(double d, double e) {
		if (this.size <= 0) {
			return d;
		} else {
			double f = (e - (double)this.offset) / (double)this.size;
			return MathHelper.clampedLerp(this.target, d, f);
		}
	}
}
