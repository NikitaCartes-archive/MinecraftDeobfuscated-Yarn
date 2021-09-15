package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public class SlideConfig {
	public static final Codec<SlideConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("target").forGetter(slideConfig -> slideConfig.target),
					Codecs.NONNEGATIVE_INT.fieldOf("size").forGetter(slideConfig -> slideConfig.size),
					Codec.INT.fieldOf("offset").forGetter(slideConfig -> slideConfig.offset)
				)
				.apply(instance, SlideConfig::new)
	);
	private final double target;
	private final int size;
	private final int offset;

	public SlideConfig(double d, int i, int j) {
		this.target = d;
		this.size = i;
		this.offset = j;
	}

	public double method_38414(double d, int i) {
		if (this.size <= 0) {
			return d;
		} else {
			double e = (double)(i - this.offset) / (double)this.size;
			return MathHelper.clampedLerp(this.target, d, e);
		}
	}
}
