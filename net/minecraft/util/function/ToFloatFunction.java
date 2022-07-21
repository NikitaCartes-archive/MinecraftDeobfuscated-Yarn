/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
    public static final ToFloatFunction<Float> IDENTITY = ToFloatFunction.fromFloat(value -> value);

    public float apply(C var1);

    public float min();

    public float max();

    public static ToFloatFunction<Float> fromFloat(final Float2FloatFunction delegate) {
        return new ToFloatFunction<Float>(){

            @Override
            public float apply(Float float_) {
                return ((Float)delegate.apply(float_)).floatValue();
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
    default public <C2> ToFloatFunction<C2> compose(final Function<C2, C> before) {
        final ToFloatFunction toFloatFunction = this;
        return new ToFloatFunction<C2>(){

            @Override
            public float apply(C2 x) {
                return toFloatFunction.apply(before.apply(x));
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

