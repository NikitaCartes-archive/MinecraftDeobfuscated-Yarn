package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
	ToFloatFunction<Float> IDENTITY = fromFloat(value -> value);

	float apply(C x);

	float min();

	float max();

	static ToFloatFunction<Float> fromFloat(Float2FloatFunction delegate) {
		return new ToFloatFunction<Float>() {
			public float apply(Float float_) {
				return delegate.apply(float_);
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

	/**
	 * {@return a composed function that first applies the before function to its input,
	 * and then applies this function} to the result.
	 * 
	 * @param before the function to apply before this function is applied
	 */
	default <C2> ToFloatFunction<C2> compose(Function<C2, C> before) {
		final ToFloatFunction<C> toFloatFunction = this;
		return new ToFloatFunction<C2>() {
			@Override
			public float apply(C2 x) {
				return toFloatFunction.apply((C)before.apply(x));
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
