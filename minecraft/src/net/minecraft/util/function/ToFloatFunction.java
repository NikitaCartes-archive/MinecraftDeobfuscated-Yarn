package net.minecraft.util.function;

import net.minecraft.util.math.Spline;

public interface ToFloatFunction<C> {
	float apply(C object);

	default ToFloatFunction<C> combine(ToFloatFunction<C> other, Spline.FloatBinaryOperator floatBinaryOperator) {
		return object -> floatBinaryOperator.combine(this.apply(object), other.apply(object));
	}
}
