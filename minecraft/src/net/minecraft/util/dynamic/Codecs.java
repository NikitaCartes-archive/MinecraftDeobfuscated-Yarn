package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntryList;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
	public static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
	public static final Codec<Integer> NONNEGATIVE_INT = rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
	public static final Codec<Integer> POSITIVE_INT = rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
	public static final Codec<Float> POSITIVE_FLOAT = rangedFloat(0.0F, Float.MAX_VALUE, v -> "Value must be positive: " + v);
	public static final Codec<Pattern> REGULAR_EXPRESSION = Codec.STRING.comapFlatMap(pattern -> {
		try {
			return DataResult.success(Pattern.compile(pattern));
		} catch (PatternSyntaxException var2) {
			return DataResult.error("Invalid regex pattern '" + pattern + "': " + var2.getMessage());
		}
	}, Pattern::pattern);
	public static final Codec<Instant> INSTANT = instant(DateTimeFormatter.ISO_INSTANT);
	public static final Codec<byte[]> BASE_64 = Codec.STRING.comapFlatMap(encoded -> {
		try {
			return DataResult.success(Base64.getDecoder().decode(encoded));
		} catch (IllegalArgumentException var2) {
			return DataResult.error("Malformed base64 string");
		}
	}, data -> Base64.getEncoder().encodeToString(data));
	public static final Codec<Codecs.TagEntryId> TAG_ENTRY_ID = Codec.STRING
		.comapFlatMap(
			tagEntry -> tagEntry.startsWith("#")
					? Identifier.validate(tagEntry.substring(1)).map(id -> new Codecs.TagEntryId(id, true))
					: Identifier.validate(tagEntry).map(id -> new Codecs.TagEntryId(id, false)),
			Codecs.TagEntryId::asString
		);
	public static final Function<Optional<Long>, OptionalLong> OPTIONAL_OF_LONG_TO_OPTIONAL_LONG = optional -> (OptionalLong)optional.map(OptionalLong::of)
			.orElseGet(OptionalLong::empty);
	public static final Function<OptionalLong, Optional<Long>> OPTIONAL_LONG_TO_OPTIONAL_OF_LONG = optionalLong -> optionalLong.isPresent()
			? Optional.of(optionalLong.getAsLong())
			: Optional.empty();

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
			}), pair -> ImmutableList.of(leftFunction.apply(pair), rightFunction.apply(pair)));
		Codec<I> codec3 = RecordCodecBuilder.create(
				instance -> instance.group(codec.fieldOf(leftFieldName).forGetter(Pair::getFirst), codec.fieldOf(rightFieldName).forGetter(Pair::getSecond))
						.apply(instance, Pair::of)
			)
			.comapFlatMap(
				pair -> (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond()), pair -> Pair.of(leftFunction.apply(pair), rightFunction.apply(pair))
			);
		Codec<I> codec4 = new Codecs.Either<>(codec2, codec3).xmap(either -> either.map(object -> object, object -> object), com.mojang.datafixers.util.Either::left);
		return Codec.either(codec, codec4)
			.comapFlatMap(either -> either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), pair -> {
				P object = (P)leftFunction.apply(pair);
				P object2 = (P)rightFunction.apply(pair);
				return Objects.equals(object, object2) ? com.mojang.datafixers.util.Either.left(object) : com.mojang.datafixers.util.Either.right(pair);
			});
	}

	public static <A> ResultFunction<A> orElsePartial(A object) {
		return new ResultFunction<A>() {
			@Override
			public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> result) {
				MutableObject<String> mutableObject = new MutableObject<>();
				Optional<Pair<A, T>> optional = result.resultOrPartial(mutableObject::setValue);
				return optional.isPresent() ? result : DataResult.error("(" + mutableObject.getValue() + " -> using default)", Pair.of(object, input));
			}

			@Override
			public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> result) {
				return result;
			}

			public String toString() {
				return "OrElsePartial[" + object + "]";
			}
		};
	}

	public static <E> Codec<E> rawIdChecked(ToIntFunction<E> elementToRawId, IntFunction<E> rawIdToElement, int errorRawId) {
		return Codec.INT
			.flatXmap(
				rawId -> (DataResult)Optional.ofNullable(rawIdToElement.apply(rawId))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown element id: " + rawId)),
				element -> {
					int j = elementToRawId.applyAsInt(element);
					return j == errorRawId ? DataResult.error("Element with unknown id: " + element) : DataResult.success(j);
				}
			);
	}

	public static <E> Codec<E> idChecked(Function<E, String> elementToId, Function<String, E> idToElement) {
		return Codec.STRING
			.flatXmap(
				id -> (DataResult)Optional.ofNullable(idToElement.apply(id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name:" + id)),
				element -> (DataResult)Optional.ofNullable((String)elementToId.apply(element))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Element with unknown name: " + element))
			);
	}

	public static <E> Codec<E> orCompressed(Codec<E> uncompressedCodec, Codec<E> compressedCodec) {
		return new Codec<E>() {
			@Override
			public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
				return ops.compressMaps() ? compressedCodec.encode(input, ops, prefix) : uncompressedCodec.encode(input, ops, prefix);
			}

			@Override
			public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
				return ops.compressMaps() ? compressedCodec.decode(ops, input) : uncompressedCodec.decode(ops, input);
			}

			public String toString() {
				return uncompressedCodec + " orCompressed " + compressedCodec;
			}
		};
	}

	public static <E> Codec<E> withLifecycle(Codec<E> originalCodec, Function<E, Lifecycle> entryLifecycleGetter, Function<E, Lifecycle> lifecycleGetter) {
		return originalCodec.mapResult(new ResultFunction<E>() {
			@Override
			public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<E, T>> result) {
				return (DataResult<Pair<E, T>>)result.result().map(pair -> result.setLifecycle((Lifecycle)entryLifecycleGetter.apply(pair.getFirst()))).orElse(result);
			}

			@Override
			public <T> DataResult<T> coApply(DynamicOps<T> ops, E input, DataResult<T> result) {
				return result.setLifecycle((Lifecycle)lifecycleGetter.apply(input));
			}

			public String toString() {
				return "WithLifecycle[" + entryLifecycleGetter + " " + lifecycleGetter + "]";
			}
		});
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

	public static <T> Function<RegistryEntryList<T>, DataResult<RegistryEntryList<T>>> createNonEmptyEntryListChecker() {
		return entries -> entries.getStorage().right().filter(List::isEmpty).isPresent() ? DataResult.error("List must have contents") : DataResult.success(entries);
	}

	public static <T> Codec<RegistryEntryList<T>> nonEmptyEntryList(Codec<RegistryEntryList<T>> originalCodec) {
		return originalCodec.flatXmap(createNonEmptyEntryListChecker(), createNonEmptyEntryListChecker());
	}

	public static <A> Codec<A> createLazy(Supplier<Codec<A>> supplier) {
		return new Codecs.Lazy<>(supplier);
	}

	public static <E> MapCodec<E> createContextRetrievalCodec(Function<DynamicOps<?>, DataResult<E>> retriever) {
		class ContextRetrievalCodec extends MapCodec<E> {
			@Override
			public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return prefix;
			}

			@Override
			public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
				return (DataResult<E>)retriever.apply(ops);
			}

			public String toString() {
				return "ContextRetrievalCodec[" + retriever + "]";
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.empty();
			}
		}

		return new ContextRetrievalCodec();
	}

	public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> createEqualTypeChecker(Function<E, T> typeGetter) {
		return collection -> {
			Iterator<E> iterator = collection.iterator();
			if (iterator.hasNext()) {
				T object = (T)typeGetter.apply(iterator.next());

				while (iterator.hasNext()) {
					E object2 = (E)iterator.next();
					T object3 = (T)typeGetter.apply(object2);
					if (object3 != object) {
						return DataResult.error("Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object);
					}
				}
			}

			return DataResult.success(collection, Lifecycle.stable());
		};
	}

	public static <A> Codec<A> exceptionCatching(Codec<A> codec) {
		return Codec.of(codec, new Decoder<A>() {
			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
				try {
					return codec.decode(ops, input);
				} catch (Exception var4) {
					return DataResult.error("Cauch exception decoding " + input + ": " + var4.getMessage());
				}
			}
		});
	}

	public static Codec<Instant> instant(DateTimeFormatter formatter) {
		return Codec.STRING.comapFlatMap(dateTimeString -> {
			try {
				return DataResult.success(Instant.from(formatter.parse(dateTimeString)));
			} catch (Exception var3) {
				return DataResult.error(var3.getMessage());
			}
		}, formatter::format);
	}

	public static MapCodec<OptionalLong> optionalLong(MapCodec<Optional<Long>> codec) {
		return codec.xmap(OPTIONAL_OF_LONG_TO_OPTIONAL_LONG, OPTIONAL_LONG_TO_OPTIONAL_OF_LONG);
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

	static record Lazy<A>(Supplier<Codec<A>> delegate) implements Codec<A> {
		Lazy(Supplier<Codec<A>> delegate) {
			Supplier<Codec<A>> var2 = Suppliers.memoize(delegate::get);
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

	public static record TagEntryId(Identifier id, boolean tag) {
		public String toString() {
			return this.asString();
		}

		private String asString() {
			return this.tag ? "#" + this.id : this.id.toString();
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
