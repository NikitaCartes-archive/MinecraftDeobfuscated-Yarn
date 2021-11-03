package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableObject;

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
	public static final Codec<Float> POSITIVE_FLOAT = rangedFloat(0.0F, Float.MAX_VALUE, v -> "Value must be positive: " + v);

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
	public static <F, S> Codec<com.mojang.datafixers.util.Either<F, S>> xor(Codec<F> first, Codec<S> second) {
		return new Codecs.Xor<>(first, second);
	}

	public static <P, I> Codec<I> createCodecForPairObject(
		Codec<P> codec,
		String leftFieldName,
		String rightFieldName,
		BiFunction<P, P, DataResult<I>> combineFunction,
		Function<I, P> leftFunction,
		Function<I, P> rightFunction
	) {
		Codec<I> codec2 = Codec.list(codec).comapFlatMap(list -> Util.toArray(list, 2).flatMap(listx -> {
				P object = (P)listx.get(0);
				P object2 = (P)listx.get(1);
				return (DataResult)combineFunction.apply(object, object2);
			}), object -> ImmutableList.of(leftFunction.apply(object), rightFunction.apply(object)));
		Codec<I> codec3 = RecordCodecBuilder.create(
				instance -> instance.group(codec.fieldOf(leftFieldName).forGetter(Pair::getFirst), codec.fieldOf(rightFieldName).forGetter(Pair::getSecond))
						.apply(instance, Pair::of)
			)
			.comapFlatMap(
				pair -> (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond()), object -> Pair.of(leftFunction.apply(object), rightFunction.apply(object))
			);
		Codec<I> codec4 = new Codecs.Either<>(codec2, codec3).xmap(either -> either.map(object -> object, object -> object), com.mojang.datafixers.util.Either::left);
		return Codec.either(codec, codec4)
			.comapFlatMap(either -> either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), object -> {
				P object2 = (P)leftFunction.apply(object);
				P object3 = (P)rightFunction.apply(object);
				return Objects.equals(object2, object3) ? com.mojang.datafixers.util.Either.left(object2) : com.mojang.datafixers.util.Either.right(object);
			});
	}

	public static <A> ResultFunction<A> method_39028(A object) {
		return new ResultFunction<A>() {
			@Override
			public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> dynamicOps, T object, DataResult<Pair<A, T>> dataResult) {
				MutableObject<String> mutableObject = new MutableObject<>();
				Optional<Pair<A, T>> optional = dataResult.resultOrPartial(mutableObject::setValue);
				return optional.isPresent() ? dataResult : DataResult.error("(" + mutableObject.getValue() + " -> using default)", Pair.of(object, object));
			}

			@Override
			public <T> DataResult<T> coApply(DynamicOps<T> dynamicOps, A object, DataResult<T> dataResult) {
				return dataResult;
			}

			public String toString() {
				return "OrElsePartial[" + object + "]";
			}
		};
	}

	private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> createIntRangeChecker(N min, N max, Function<N, String> messageFactory) {
		return value -> ((Comparable)value).compareTo(min) >= 0 && ((Comparable)value).compareTo(max) <= 0
				? DataResult.success(value)
				: DataResult.error((String)messageFactory.apply(value));
	}

	private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
		Function<Integer, DataResult<Integer>> function = createIntRangeChecker(min, max, messageFactory);
		return Codec.INT.flatXmap(function, function);
	}

	private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> createFloatRangeChecker(N min, N max, Function<N, String> messageFactory) {
		return value -> ((Comparable)value).compareTo(min) > 0 && ((Comparable)value).compareTo(max) <= 0
				? DataResult.success(value)
				: DataResult.error((String)messageFactory.apply(value));
	}

	private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> messageFactory) {
		Function<Float, DataResult<Float>> function = createFloatRangeChecker(min, max, messageFactory);
		return Codec.FLOAT.flatXmap(function, function);
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

	public static <A> Codec<A> createLazy(Supplier<Codec<A>> supplier) {
		return new Codecs.Lazy(supplier);
	}

	static final class Either<F, S> implements Codec<com.mojang.datafixers.util.Either<F, S>> {
		private final Codec<F> first;
		private final Codec<S> second;

		public Either(Codec<F> first, Codec<S> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult = this.first
				.decode(ops, input)
				.map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
			if (!dataResult.error().isPresent()) {
				return dataResult;
			} else {
				DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult2 = this.second
					.decode(ops, input)
					.map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
				return !dataResult2.error().isPresent() ? dataResult2 : dataResult.apply2((pair, pair2) -> pair2, dataResult2);
			}
		}

		public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
			return either.map(left -> this.first.encode((F)left, dynamicOps, object), right -> this.second.encode((S)right, dynamicOps, object));
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Codecs.Either<?, ?> either = (Codecs.Either<?, ?>)o;
				return Objects.equals(this.first, either.first) && Objects.equals(this.second, either.second);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.first, this.second});
		}

		public String toString() {
			return "EitherCodec[" + this.first + ", " + this.second + "]";
		}
	}

	static record Lazy() implements Codec {
		private final Supplier<Codec<A>> delegate;

		Lazy(Supplier<Codec<A>> supplier) {
			Supplier<Codec<A>> var2 = Suppliers.memoize(supplier::get);
			this.delegate = var2;
		}

		@Override
		public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
			return ((Codec)this.delegate.get()).decode(ops, input);
		}

		@Override
		public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
			return ((Codec)this.delegate.get()).encode(input, ops, prefix);
		}
	}

	/**
	 * An xor codec that only permits exactly one of the two data choices to be
	 * present.
	 * 
	 * @see Codecs#xor(Codec, Codec)
	 * @see com.mojang.serialization.codecs.EitherCodec
	 */
	static final class Xor<F, S> implements Codec<com.mojang.datafixers.util.Either<F, S>> {
		private final Codec<F> first;
		private final Codec<S> second;

		public Xor(Codec<F> first, Codec<S> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult = this.first
				.decode(ops, input)
				.map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
			DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult2 = this.second
				.decode(ops, input)
				.map(pair -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
			Optional<Pair<com.mojang.datafixers.util.Either<F, S>, T>> optional = dataResult.result();
			Optional<Pair<com.mojang.datafixers.util.Either<F, S>, T>> optional2 = dataResult2.result();
			if (optional.isPresent() && optional2.isPresent()) {
				return DataResult.error(
					"Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(),
					(Pair<com.mojang.datafixers.util.Either<F, S>, T>)optional.get()
				);
			} else {
				return optional.isPresent() ? dataResult : dataResult2;
			}
		}

		public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
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
