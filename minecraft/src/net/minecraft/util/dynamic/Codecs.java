package net.minecraft.util.dynamic;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
	public static final Codec<Integer> NONNEGATIVE_INT = rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
	public static final Codec<Integer> POSITIVE_INT = rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);

	/**
	 * Returns an exclusive-or codec for {@link Either} instances.
	 * 
	 * <p>This returned codec fails if both the {@code first} and {@code second} codecs can
	 * decode the input, while DFU's {@link com.mojang.serialization.codecs.EitherCodec}
	 * will always take the first decoded result when it is available.
	 * 
	 * <p>Otherwise, this behaves the same as the either codec.
	 * 
	 * @param <F> the first type
	 * @param <S> the second type
	 * @return the xor codec for the two codecs
	 * @see Codec#either(Codec, Codec)
	 * @see com.mojang.serialization.codecs.EitherCodec
	 * 
	 * @param first the first codec
	 * @param second the second codec
	 */
	public static <F, S> Codec<Either<F, S>> xor(Codec<F> first, Codec<S> second) {
		return new Codecs.Xor<>(first, second);
	}

	private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> createRangeChecker(N min, N max, Function<N, String> messageFactory) {
		return value -> ((Comparable)value).compareTo(min) >= 0 && ((Comparable)value).compareTo(max) <= 0
				? DataResult.success(value)
				: DataResult.error((String)messageFactory.apply(value));
	}

	private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
		Function<Integer, DataResult<Integer>> function = createRangeChecker(min, max, messageFactory);
		return Codec.INT.flatXmap(function, function);
	}

	public static <T> Function<List<T>, DataResult<List<T>>> createNonEmptyListChecker() {
		return list -> list.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(list);
	}

	public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> originalCodec) {
		return originalCodec.flatXmap(createNonEmptyListChecker(), createNonEmptyListChecker());
	}

	public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> createPresentValuesChecker() {
		return suppliers -> {
			List<String> list = Lists.<String>newArrayList();

			for (int i = 0; i < suppliers.size(); i++) {
				Supplier<T> supplier = (Supplier<T>)suppliers.get(i);

				try {
					if (supplier.get() == null) {
						list.add("Missing value [" + i + "] : " + supplier);
					}
				} catch (Exception var5) {
					list.add("Invalid value [" + i + "]: " + supplier + ", message: " + var5.getMessage());
				}
			}

			return !list.isEmpty() ? DataResult.error(String.join("; ", list)) : DataResult.success(suppliers, Lifecycle.stable());
		};
	}

	public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> createPresentValueChecker() {
		return supplier -> {
			try {
				if (supplier.get() == null) {
					return DataResult.error("Missing value: " + supplier);
				}
			} catch (Exception var2) {
				return DataResult.error("Invalid value: " + supplier + ", message: " + var2.getMessage());
			}

			return DataResult.success(supplier, Lifecycle.stable());
		};
	}

	/**
	 * An xor codec that only permits exactly one of the two data choices to be
	 * present.
	 * 
	 * @see Codecs#xor(Codec, Codec)
	 * @see com.mojang.serialization.codecs.EitherCodec
	 */
	static final class Xor<F, S> implements Codec<Either<F, S>> {
		private final Codec<F> first;
		private final Codec<S> second;

		public Xor(Codec<F> first, Codec<S> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<Either<F, S>, T>> dataResult = this.first.decode(ops, input).map(pair -> pair.mapFirst(Either::left));
			DataResult<Pair<Either<F, S>, T>> dataResult2 = this.second.decode(ops, input).map(pair -> pair.mapFirst(Either::right));
			Optional<Pair<Either<F, S>, T>> optional = dataResult.result();
			Optional<Pair<Either<F, S>, T>> optional2 = dataResult2.result();
			if (optional.isPresent() && optional2.isPresent()) {
				return DataResult.error(
					"Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(),
					(Pair<Either<F, S>, T>)optional.get()
				);
			} else {
				return optional.isPresent() ? dataResult : dataResult2;
			}
		}

		public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
			return either.map(left -> this.first.encode((F)left, dynamicOps, object), right -> this.second.encode((S)right, dynamicOps, object));
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Codecs.Xor<?, ?> xor = (Codecs.Xor<?, ?>)o;
				return Objects.equals(this.first, xor.first) && Objects.equals(this.second, xor.second);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.first, this.second});
		}

		public String toString() {
			return "XorCodec[" + this.first + ", " + this.second + "]";
		}
	}
}
