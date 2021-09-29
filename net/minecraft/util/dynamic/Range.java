/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.util.dynamic.Codecs;

public record Range<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
    public static final Codec<Range<Integer>> CODEC = Range.createCodec(Codec.INT);

    public Range {
        if (minInclusive.compareTo(maxInclusive) > 0) {
            throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
        }
    }

    public static <T extends Comparable<T>> Codec<Range<T>> createCodec(Codec<T> elementCodec) {
        return Codecs.createCodecForPairObject(elementCodec, "min_inclusive", "max_inclusive", Range::validate, Range::minInclusive, Range::maxInclusive);
    }

    public static <T extends Comparable<T>> Codec<Range<T>> createRangedCodec(Codec<T> codec, T minInclusive, T maxInclusive) {
        Function<Range, DataResult> function = range -> {
            if (range.minInclusive().compareTo(minInclusive) < 0) {
                return DataResult.error("Range limit too low, expected at least " + minInclusive + " [" + range.minInclusive() + "-" + range.maxInclusive() + "]");
            }
            if (range.maxInclusive().compareTo(maxInclusive) > 0) {
                return DataResult.error("Range limit too high, expected at most " + maxInclusive + " [" + range.minInclusive() + "-" + range.maxInclusive() + "]");
            }
            return DataResult.success(range);
        };
        return Range.createCodec(codec).flatXmap(function, function);
    }

    public static <T extends Comparable<T>> DataResult<Range<T>> validate(T minInclusive, T maxInclusive) {
        if (minInclusive.compareTo(maxInclusive) <= 0) {
            return DataResult.success(new Range<T>(minInclusive, maxInclusive));
        }
        return DataResult.error("min_inclusive must be less than or equal to max_inclusive");
    }

    public boolean contains(T value) {
        return value.compareTo(this.minInclusive) >= 0 && value.compareTo(this.maxInclusive) <= 0;
    }

    public boolean contains(Range<T> other) {
        return other.minInclusive().compareTo(this.minInclusive) >= 0 && other.maxInclusive.compareTo(this.maxInclusive) <= 0;
    }

    @Override
    public String toString() {
        return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
    }
}

