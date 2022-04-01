package net.minecraft.util.function;

@FunctionalInterface
public interface ToFloatFunction<C> {
	float apply(C x);
}
