package net.minecraft.util.function;

import net.minecraft.util.math.Spline;

public interface ToFloatFunction<C> {
	float apply(C x);

	default ToFloatFunction<C> combine(ToFloatFunction<C> other, Spline.FloatBinaryOperator combineFunction) {
		return x -> combineFunction.combine(this.apply(x), other.apply(x));
	}
}
