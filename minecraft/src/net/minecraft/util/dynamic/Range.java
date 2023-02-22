package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record Range<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
	public static final Codec<Range<Integer>> CODEC = createCodec((Codec<T>)Codec.INT);

	public Range(T minInclusive, T maxInclusive) {
		if (minInclusive.compareTo(maxInclusive) > 0) {
			throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
		} else {
			this.minInclusive = minInclusive;
			this.maxInclusive = maxInclusive;
		}
	}

	public static <T extends Comparable<T>> Codec<Range<T>> createCodec(Codec<T> elementCodec) {
		return Codecs.createCodecForPairObject(elementCodec, "min_inclusive", "max_inclusive", Range::validate, Range::minInclusive, Range::maxInclusive);
	}

	public static <T extends Comparable<T>> Codec<Range<T>> createRangedCodec(Codec<T> codec, T minInclusive, T maxInclusive) {
		return Codecs.validate(
			createCodec(codec),
			range -> {
				if (range.minInclusive().compareTo(minInclusive) < 0) {
					return DataResult.error(() -> "Range limit too low, expected at least " + minInclusive + " [" + range.minInclusive() + "-" + range.maxInclusive() + "]");
				} else {
					return range.maxInclusive().compareTo(maxInclusive) > 0
						? DataResult.error(() -> "Range limit too high, expected at most " + maxInclusive + " [" + range.minInclusive() + "-" + range.maxInclusive() + "]")
						: DataResult.success(range);
				}
			}
		);
	}

	public static <T extends Comparable<T>> DataResult<Range<T>> validate(T minInclusive, T maxInclusive) {
		return minInclusive.compareTo(maxInclusive) <= 0
			? DataResult.success(new Range<>(minInclusive, maxInclusive))
			: DataResult.error(() -> "min_inclusive must be less than or equal to max_inclusive");
	}

	public boolean contains(T value) {
		return value.compareTo(this.minInclusive) >= 0 && value.compareTo(this.maxInclusive) <= 0;
	}

	public boolean contains(Range<T> other) {
		return other.minInclusive().compareTo(this.minInclusive) >= 0 && other.maxInclusive.compareTo(this.maxInclusive) <= 0;
	}

	public String toString() {
		return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
	}
}
