/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {
    public static final ToFloatFunction<Float> field_37409 = ToFloatFunction.method_41308(f -> f);

    public float apply(C var1);

    public float min();

    public float max();

    public static ToFloatFunction<Float> method_41308(final Float2FloatFunction float2FloatFunction) {
        return new ToFloatFunction<Float>(){

            @Override
            public float apply(Float float_) {
                return ((Float)float2FloatFunction.apply(float_)).floatValue();
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

    default public <C2> ToFloatFunction<C2> method_41309(final Function<C2, C> function) {
        final ToFloatFunction toFloatFunction = this;
        return new ToFloatFunction<C2>(){

            @Override
            public float apply(C2 x) {
                return toFloatFunction.apply(function.apply(x));
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

