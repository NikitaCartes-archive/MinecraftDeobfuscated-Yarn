package net.minecraft.util.dynamic;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
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
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
	public static final Codec<JsonElement> JSON_ELEMENT = Codec.PASSTHROUGH
		.xmap(dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(), element -> new Dynamic<>(JsonOps.INSTANCE, element));
	public static final Codec<Text> TEXT = JSON_ELEMENT.flatXmap(element -> {
		try {
			return DataResult.success(Text.Serializer.fromJson(element));
		} catch (JsonParseException var2) {
			return DataResult.error(var2.getMessage());
		}
	}, text -> {
		try {
			return DataResult.success(Text.Serializer.toJsonTree(text));
		} catch (IllegalArgumentException var2) {
			return DataResult.error(var2.getMessage());
		}
	});
	public static final Codec<Vector3f> VECTOR_3F = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.toArray(list, 3).map(listx -> new Vector3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2))),
			vec3f -> List.of(vec3f.x(), vec3f.y(), vec3f.z())
		);
	public static final Codec<Quaternionf> QUATERNIONF = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.toArray(list, 4).map(listx -> new Quaternionf((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2), (Float)listx.get(3))),
			quaternion -> List.of(quaternion.x, quaternion.y, quaternion.z, quaternion.w)
		);
	public static final Codec<AxisAngle4f> AXIS_ANGLE4F = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("angle").forGetter(axisAngle -> axisAngle.angle),
					VECTOR_3F.fieldOf("axis").forGetter(axisAngle -> new Vector3f(axisAngle.x, axisAngle.y, axisAngle.z))
				)
				.apply(instance, AxisAngle4f::new)
	);
	public static final Codec<Quaternionf> ROTATION = Codec.either(QUATERNIONF, AXIS_ANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new))
		.xmap(either -> either.map(quaternion -> quaternion, quaternion -> quaternion), com.mojang.datafixers.util.Either::left);
	public static Codec<Matrix4f> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap(list -> Util.toArray(list, 16).map(listx -> {
			Matrix4f matrix4f = new Matrix4f();

			for (int i = 0; i < listx.size(); i++) {
				matrix4f.setRowColumn(i >> 2, i & 3, (Float)listx.get(i));
			}

			return matrix4f.determineProperties();
		}), matrix4f -> {
		FloatList floatList = new FloatArrayList(16);

		for (int i = 0; i < 16; i++) {
			floatList.add(matrix4f.getRowColumn(i >> 2, i & 3));
		}

		return floatList;
	});
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
	public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap(stream -> BitSet.valueOf(stream.toArray()), set -> Arrays.stream(set.toLongArray()));
	private static final Codec<Property> GAME_PROFILE_PROPERTY = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(Property::getName),
					Codec.STRING.fieldOf("value").forGetter(Property::getValue),
					Codec.STRING.optionalFieldOf("signature").forGetter(property -> Optional.ofNullable(property.getSignature()))
				)
				.apply(instance, (key, value, signature) -> new Property(key, value, (String)signature.orElse(null)))
	);
	@VisibleForTesting
	public static final Codec<PropertyMap> GAME_PROFILE_PROPERTY_MAP = Codec.either(
			Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), GAME_PROFILE_PROPERTY.listOf()
		)
		.xmap(either -> {
			PropertyMap propertyMap = new PropertyMap();
			either.ifLeft(map -> map.forEach((key, values) -> {
					for (String string : values) {
						propertyMap.put(key, new Property(key, string));
					}
				})).ifRight(properties -> {
				for (Property property : properties) {
					propertyMap.put(property.getName(), property);
				}
			});
			return propertyMap;
		}, properties -> com.mojang.datafixers.util.Either.right(properties.values().stream().toList()));
	public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.mapPair(
							Uuids.CODEC.<Optional>xmap(Optional::of, optional -> (UUID)optional.orElse(null)).optionalFieldOf("id", Optional.empty()),
							Codec.STRING.<Optional>xmap(Optional::of, optional -> (String)optional.orElse(null)).optionalFieldOf("name", Optional.empty())
						)
						.flatXmap(Codecs::createGameProfileFromPair, Codecs::createPairFromGameProfile)
						.forGetter(Function.identity()),
					GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
				)
				.apply(instance, (profile, properties) -> {
					properties.forEach((key, property) -> profile.getProperties().put(key, property));
					return profile;
				})
	);
	public static final Codec<String> NON_EMPTY_STRING = validate(
		Codec.STRING, string -> string.isEmpty() ? DataResult.error("Expected non-empty string") : DataResult.success(string)
	);

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

	public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> validator) {
		return codec.flatXmap(validator, validator);
	}

	private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
		return validate(
			Codec.INT,
			value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error((String)messageFactory.apply(value))
		);
	}

	public static Codec<Integer> rangedInt(int min, int max) {
		return rangedInt(min, max, value -> "Value must be within range [" + min + ";" + max + "]: " + value);
	}

	private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> messageFactory) {
		return validate(
			Codec.FLOAT,
			value -> value.compareTo(min) > 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error((String)messageFactory.apply(value))
		);
	}

	public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> originalCodec) {
		return validate(originalCodec, list -> list.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(list));
	}

	public static <T> Codec<RegistryEntryList<T>> nonEmptyEntryList(Codec<RegistryEntryList<T>> originalCodec) {
		return validate(
			originalCodec,
			entryList -> entryList.getStorage().right().filter(List::isEmpty).isPresent() ? DataResult.error("List must have contents") : DataResult.success(entryList)
		);
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

	private static DataResult<GameProfile> createGameProfileFromPair(Pair<Optional<UUID>, Optional<String>> pair) {
		try {
			return DataResult.success(new GameProfile((UUID)pair.getFirst().orElse(null), (String)pair.getSecond().orElse(null)));
		} catch (Throwable var2) {
			return DataResult.error(var2.getMessage());
		}
	}

	private static DataResult<Pair<Optional<UUID>, Optional<String>>> createPairFromGameProfile(GameProfile profile) {
		return DataResult.success(Pair.of(Optional.ofNullable(profile.getId()), Optional.ofNullable(profile.getName())));
	}

	public static Codec<String> string(int minLength, int maxLength) {
		return validate(
			Codec.STRING,
			string -> {
				int k = string.length();
				if (k < minLength) {
					return DataResult.error("String \"" + string + "\" is too short: " + k + ", expected range [" + minLength + "-" + maxLength + "]");
				} else {
					return k > maxLength
						? DataResult.error("String \"" + string + "\" is too long: " + k + ", expected range [" + minLength + "-" + maxLength + "]")
						: DataResult.success(string);
				}
			}
		);
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
