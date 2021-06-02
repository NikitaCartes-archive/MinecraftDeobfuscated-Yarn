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
 * <p>Expect its removal once Mojang updates DataFixerUpper.
 */
public class Codecs {
	public static final Codec<Integer> field_33441 = method_36241(0, Integer.MAX_VALUE, integer -> "Value must be non-negative: " + integer);
	public static final Codec<Integer> field_33442 = method_36241(1, Integer.MAX_VALUE, integer -> "Value must be positive: " + integer);

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

	private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> method_36243(N number, N number2, Function<N, String> function) {
		return number3 -> ((Comparable)number3).compareTo(number) >= 0 && ((Comparable)number3).compareTo(number2) <= 0
				? DataResult.success(number3)
				: DataResult.error((String)function.apply(number3));
	}

	private static Codec<Integer> method_36241(int i, int j, Function<Integer, String> function) {
		Function<Integer, DataResult<Integer>> function2 = method_36243(i, j, function);
		return Codec.INT.flatXmap(function2, function2);
	}

	public static <T> Function<List<T>, DataResult<List<T>>> method_36240() {
		return list -> list.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(list);
	}

	public static <T> Codec<List<T>> method_36973(Codec<List<T>> codec) {
		return codec.flatXmap(method_36240(), method_36240());
	}

	public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> method_37351() {
		return list -> {
			List<String> list2 = Lists.<String>newArrayList();

			for (int i = 0; i < list.size(); i++) {
				Supplier<T> supplier = (Supplier<T>)list.get(i);

				try {
					if (supplier.get() == null) {
						list2.add("Missing value [" + i + "] : " + supplier);
					}
				} catch (Exception var5) {
					list2.add("Invalid value [" + i + "]: " + supplier + ", message: " + var5.getMessage());
				}
			}

			return !list2.isEmpty() ? DataResult.error(String.join("; ", list2)) : DataResult.success(list, Lifecycle.stable());
		};
	}

	public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> method_37352() {
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
