package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
	ToFloatFunction<Float> field_37409 = method_41308(f -> f);

	float apply(C x);

	float min();

	float max();

	static ToFloatFunction<Float> method_41308(Float2FloatFunction float2FloatFunction) {
		return new ToFloatFunction<Float>() {
			public float apply(Float float_) {
				return float2FloatFunction.apply(float_);
			}

			@Override
			public float min() {
				return Float.NEGATIVE_INFINITY;
			}

			@Override
			public float max() {
				return Float.POSITIVE_INFINITY;
			}
		};
	}

	default <C2> ToFloatFunction<C2> method_41309(Function<C2, C> function) {
		final ToFloatFunction<C> toFloatFunction = this;
		return new ToFloatFunction<C2>() {
			@Override
			public float apply(C2 x) {
				return toFloatFunction.apply((C)function.apply(x));
			}

			@Override
			public float min() {
				return toFloatFunction.min();
			}

			@Override
			public float max() {
				return toFloatFunction.max();
			}
		};
	}
}
