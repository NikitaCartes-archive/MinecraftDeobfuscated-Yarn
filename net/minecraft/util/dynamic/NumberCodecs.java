/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public class NumberCodecs {
    private static Function<Integer, DataResult<Integer>> createRangedDataResultFactory(int min, int max) {
        return integer -> {
            if (integer >= min && integer <= max) {
                return DataResult.success(integer);
            }
            return DataResult.error("Value " + integer + " outside of range [" + min + ":" + max + "]", integer);
        };
    }

    public static Codec<Integer> rangedInt(int min, int max) {
        Function<Integer, DataResult<Integer>> function = NumberCodecs.createRangedDataResultFactory(min, max);
        return Codec.INT.flatXmap(function, function);
    }

    private static Function<Double, DataResult<Double>> createRangedDataResultFactory(double min, double max) {
        return double_ -> {
            if (double_ >= min && double_ <= max) {
                return DataResult.success(double_);
            }
            return DataResult.error("Value " + double_ + " outside of range [" + min + ":" + max + "]", double_);
        };
    }

    public static Codec<Double> rangedDouble(double min, double max) {
        Function<Double, DataResult<Double>> function = NumberCodecs.createRangedDataResultFactory(min, max);
        return Codec.DOUBLE.flatXmap(function, function);
    }
}

