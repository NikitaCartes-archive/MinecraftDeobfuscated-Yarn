package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record DistancePredicate(
	NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z, NumberRange.DoubleRange horizontal, NumberRange.DoubleRange absolute
) {
	public static final Codec<DistancePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "x", NumberRange.DoubleRange.ANY).forGetter(DistancePredicate::x),
					Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "y", NumberRange.DoubleRange.ANY).forGetter(DistancePredicate::y),
					Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "z", NumberRange.DoubleRange.ANY).forGetter(DistancePredicate::z),
					Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "horizontal", NumberRange.DoubleRange.ANY).forGetter(DistancePredicate::horizontal),
					Codecs.createStrictOptionalFieldCodec(NumberRange.DoubleRange.CODEC, "absolute", NumberRange.DoubleRange.ANY).forGetter(DistancePredicate::absolute)
				)
				.apply(instance, DistancePredicate::new)
	);

	public static DistancePredicate horizontal(NumberRange.DoubleRange horizontal) {
		return new DistancePredicate(NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, horizontal, NumberRange.DoubleRange.ANY);
	}

	public static DistancePredicate y(NumberRange.DoubleRange y) {
		return new DistancePredicate(NumberRange.DoubleRange.ANY, y, NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY);
	}

	public static DistancePredicate absolute(NumberRange.DoubleRange absolute) {
		return new DistancePredicate(NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, NumberRange.DoubleRange.ANY, absolute);
	}

	public boolean test(double x0, double y0, double z0, double x1, double y1, double z1) {
		float f = (float)(x0 - x1);
		float g = (float)(y0 - y1);
		float h = (float)(z0 - z1);
		if (!this.x.test((double)MathHelper.abs(f)) || !this.y.test((double)MathHelper.abs(g)) || !this.z.test((double)MathHelper.abs(h))) {
			return false;
		} else if (!this.horizontal.testSqrt((double)(f * f + h * h))) {
			return false;
		} else {
			return this.absolute.testSqrt((double)(f * f + g * g + h * h));
		}
	}
}
