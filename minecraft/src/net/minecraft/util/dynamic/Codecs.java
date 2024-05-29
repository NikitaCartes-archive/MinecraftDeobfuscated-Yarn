package net.minecraft.util.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
	public static final Codec<JsonElement> JSON_ELEMENT = fromOps(JsonOps.INSTANCE);
	/**
	 * A passthrough codec for a basic object. See {@link RuntimeOps} for
	 * types of objects this can "serialize".
	 * 
	 * @see RuntimeOps
	 */
	public static final Codec<Object> BASIC_OBJECT = fromOps(JavaOps.INSTANCE);
	public static final Codec<Vector3f> VECTOR_3F = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.decodeFixedLengthList(list, 3).map(listx -> new Vector3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2))),
			vec3f -> List.of(vec3f.x(), vec3f.y(), vec3f.z())
		);
	public static final Codec<Vector4f> VECTOR_4F = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.decodeFixedLengthList(list, 4).map(listx -> new Vector4f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2), (Float)listx.get(3))),
			vec4f -> List.of(vec4f.x(), vec4f.y(), vec4f.z(), vec4f.w())
		);
	public static final Codec<Quaternionf> QUATERNIONF = Codec.FLOAT
		.listOf()
		.comapFlatMap(
			list -> Util.decodeFixedLengthList(list, 4)
					.map(listx -> new Quaternionf((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2), (Float)listx.get(3)).normalize()),
			quaternion -> List.of(quaternion.x, quaternion.y, quaternion.z, quaternion.w)
		);
	public static final Codec<AxisAngle4f> AXIS_ANGLE4F = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("angle").forGetter(axisAngle -> axisAngle.angle),
					VECTOR_3F.fieldOf("axis").forGetter(axisAngle -> new Vector3f(axisAngle.x, axisAngle.y, axisAngle.z))
				)
				.apply(instance, AxisAngle4f::new)
	);
	public static final Codec<Quaternionf> ROTATION = Codec.withAlternative(QUATERNIONF, AXIS_ANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
	public static Codec<Matrix4f> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap(list -> Util.decodeFixedLengthList(list, 16).map(listx -> {
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
	public static final Codec<Integer> ARGB = Codec.withAlternative(
		Codec.INT, VECTOR_4F, vec4f -> ColorHelper.Argb.fromFloats(vec4f.w(), vec4f.x(), vec4f.y(), vec4f.z())
	);
	public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE
		.flatComapMap(
			UnsignedBytes::toInt,
			value -> value > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + value + " > 255") : DataResult.success(value.byteValue())
		);
	public static final Codec<Integer> NONNEGATIVE_INT = rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
	public static final Codec<Integer> POSITIVE_INT = rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
	public static final Codec<Float> POSITIVE_FLOAT = rangedFloat(0.0F, Float.MAX_VALUE, v -> "Value must be positive: " + v);
	public static final Codec<Pattern> REGULAR_EXPRESSION = Codec.STRING.comapFlatMap(pattern -> {
		try {
			return DataResult.success(Pattern.compile(pattern));
		} catch (PatternSyntaxException var2) {
			return DataResult.error(() -> "Invalid regex pattern '" + pattern + "': " + var2.getMessage());
		}
	}, Pattern::pattern);
	public static final Codec<Instant> INSTANT = formattedTime(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
	public static final Codec<byte[]> BASE_64 = Codec.STRING.comapFlatMap(encoded -> {
		try {
			return DataResult.success(Base64.getDecoder().decode(encoded));
		} catch (IllegalArgumentException var2) {
			return DataResult.error(() -> "Malformed base64 string");
		}
	}, data -> Base64.getEncoder().encodeToString(data));
	public static final Codec<String> ESCAPED_STRING = Codec.STRING
		.comapFlatMap(string -> DataResult.success(StringEscapeUtils.unescapeJava(string)), StringEscapeUtils::escapeJava);
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
					Codec.STRING.fieldOf("name").forGetter(Property::name),
					Codec.STRING.fieldOf("value").forGetter(Property::value),
					Codec.STRING.lenientOptionalFieldOf("signature").forGetter(property -> Optional.ofNullable(property.signature()))
				)
				.apply(instance, (key, value, signature) -> new Property(key, value, (String)signature.orElse(null)))
	);
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
					propertyMap.put(property.name(), property);
				}
			});
			return propertyMap;
		}, properties -> Either.right(properties.values().stream().toList()));
	public static final Codec<String> PLAYER_NAME = Codec.string(0, 16)
		.validate(
			name -> StringHelper.isValidPlayerName(name)
					? DataResult.success(name)
					: DataResult.error(() -> "Player name contained disallowed characters: '" + name + "'")
		);
	private static final MapCodec<GameProfile> GAME_PROFILE = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Uuids.CODEC.fieldOf("id").forGetter(GameProfile::getId), PLAYER_NAME.fieldOf("name").forGetter(GameProfile::getName))
				.apply(instance, GameProfile::new)
	);
	public static final Codec<GameProfile> GAME_PROFILE_WITH_PROPERTIES = RecordCodecBuilder.create(
		instance -> instance.group(
					GAME_PROFILE.forGetter(Function.identity()),
					GAME_PROFILE_PROPERTY_MAP.lenientOptionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
				)
				.apply(instance, (profile, properties) -> {
					properties.forEach((key, property) -> profile.getProperties().put(key, property));
					return profile;
				})
	);
	public static final Codec<String> NON_EMPTY_STRING = Codec.STRING
		.validate(string -> string.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success(string));
	public static final Codec<Integer> CODEPOINT = Codec.STRING.comapFlatMap(string -> {
		int[] is = string.codePoints().toArray();
		return is.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + string) : DataResult.success(is[0]);
	}, Character::toString);
	public static Codec<String> IDENTIFIER_PATH = Codec.STRING
		.validate(
			path -> !Identifier.isPathValid(path) ? DataResult.error(() -> "Invalid string to use as a resource path element: " + path) : DataResult.success(path)
		);

	public static <T> Codec<T> fromOps(DynamicOps<T> ops) {
		return Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(ops).getValue(), object -> new Dynamic<>(ops, (T)object));
	}

	public static <P, I> Codec<I> createCodecForPairObject(
		Codec<P> codec,
		String leftFieldName,
		String rightFieldName,
		BiFunction<P, P, DataResult<I>> combineFunction,
		Function<I, P> leftFunction,
		Function<I, P> rightFunction
	) {
		Codec<I> codec2 = Codec.list(codec).comapFlatMap(list -> Util.decodeFixedLengthList(list, 2).flatMap(listx -> {
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
		Codec<I> codec4 = Codec.withAlternative(codec2, codec3);
		return Codec.either(codec, codec4)
			.comapFlatMap(either -> either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), pair -> {
				P object = (P)leftFunction.apply(pair);
				P object2 = (P)rightFunction.apply(pair);
				return Objects.equals(object, object2) ? Either.left(object) : Either.right(pair);
			});
	}

	public static <A> ResultFunction<A> orElsePartial(A object) {
		return new ResultFunction<A>() {
			@Override
			public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> result) {
				MutableObject<String> mutableObject = new MutableObject<>();
				Optional<Pair<A, T>> optional = result.resultOrPartial(mutableObject::setValue);
				return optional.isPresent() ? result : DataResult.error(() -> "(" + mutableObject.getValue() + " -> using default)", Pair.of(object, input));
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
						.orElseGet(() -> DataResult.error(() -> "Unknown element id: " + rawId)),
				element -> {
					int j = elementToRawId.applyAsInt(element);
					return j == errorRawId ? DataResult.error(() -> "Element with unknown id: " + element) : DataResult.success(j);
				}
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

	public static <E> MapCodec<E> orCompressed(MapCodec<E> uncompressedCodec, MapCodec<E> compressedCodec) {
		return new MapCodec<E>() {
			@Override
			public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return ops.compressMaps() ? compressedCodec.encode(input, ops, prefix) : uncompressedCodec.encode(input, ops, prefix);
			}

			@Override
			public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
				return ops.compressMaps() ? compressedCodec.decode(ops, input) : uncompressedCodec.decode(ops, input);
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return compressedCodec.keys(ops);
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

	public static <E> Codec<E> withLifecycle(Codec<E> originalCodec, Function<E, Lifecycle> lifecycleGetter) {
		return withLifecycle(originalCodec, lifecycleGetter, lifecycleGetter);
	}

	public static <K, V> Codecs.StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> keyCodec, Codec<V> elementCodec) {
		return new Codecs.StrictUnboundedMapCodec<>(keyCodec, elementCodec);
	}

	private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
		return Codec.INT
			.validate(
				value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)messageFactory.apply(value))
			);
	}

	public static Codec<Integer> rangedInt(int min, int max) {
		return rangedInt(min, max, value -> "Value must be within range [" + min + ";" + max + "]: " + value);
	}

	private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> messageFactory) {
		return Codec.FLOAT
			.validate(
				value -> value.compareTo(min) > 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)messageFactory.apply(value))
			);
	}

	public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> originalCodec) {
		return originalCodec.validate(list -> list.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(list));
	}

	public static <T> Codec<RegistryEntryList<T>> nonEmptyEntryList(Codec<RegistryEntryList<T>> originalCodec) {
		return originalCodec.validate(
			entryList -> entryList.getStorage().right().filter(List::isEmpty).isPresent()
					? DataResult.error(() -> "List must have contents")
					: DataResult.success(entryList)
		);
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
						return DataResult.error(() -> "Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object);
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
					return DataResult.error(() -> "Caught exception decoding " + input + ": " + var4.getMessage());
				}
			}
		});
	}

	public static Codec<TemporalAccessor> formattedTime(DateTimeFormatter formatter) {
		return Codec.STRING.comapFlatMap(string -> {
			try {
				return DataResult.success(formatter.parse(string));
			} catch (Exception var3) {
				return DataResult.error(var3::getMessage);
			}
		}, formatter::format);
	}

	public static MapCodec<OptionalLong> optionalLong(MapCodec<Optional<Long>> codec) {
		return codec.xmap(OPTIONAL_OF_LONG_TO_OPTIONAL_LONG, OPTIONAL_LONG_TO_OPTIONAL_OF_LONG);
	}

	public static <K, V> Codec<Map<K, V>> map(Codec<Map<K, V>> codec, int maxLength) {
		return codec.validate(
			map -> map.size() > maxLength
					? DataResult.error(() -> "Map is too long: " + map.size() + ", expected range [0-" + maxLength + "]")
					: DataResult.success(map)
		);
	}

	public static <T> Codec<Object2BooleanMap<T>> object2BooleanMap(Codec<T> keyCodec) {
		return Codec.unboundedMap(keyCodec, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
	}

	@Deprecated
	public static <K, V> MapCodec<V> parameters(
		String typeKey,
		String parametersKey,
		Codec<K> typeCodec,
		Function<? super V, ? extends K> typeGetter,
		Function<? super K, ? extends Codec<? extends V>> parametersCodecGetter
	) {
		return new MapCodec<V>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.of(ops.createString(typeKey), ops.createString(parametersKey));
			}

			@Override
			public <T> DataResult<V> decode(DynamicOps<T> ops, MapLike<T> input) {
				T object = input.get(typeKey);
				return object == null ? DataResult.error(() -> "Missing \"" + typeKey + "\" in: " + input) : typeCodec.decode(ops, object).flatMap(pair -> {
					T objectx = (T)Objects.requireNonNullElseGet(input.get(parametersKey), ops::emptyMap);
					return ((Codec)parametersCodecGetter.apply(pair.getFirst())).decode(ops, objectx).map(Pair::getFirst);
				});
			}

			@Override
			public <T> RecordBuilder<T> encode(V input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				K object = (K)typeGetter.apply(input);
				prefix.add(typeKey, typeCodec.encodeStart(ops, object));
				DataResult<T> dataResult = this.encode((Codec)parametersCodecGetter.apply(object), input, ops);
				if (dataResult.result().isEmpty() || !Objects.equals(dataResult.result().get(), ops.emptyMap())) {
					prefix.add(parametersKey, dataResult);
				}

				return prefix;
			}

			private <T, V2 extends V> DataResult<T> encode(Codec<V2> codec, V value, DynamicOps<T> ops) {
				return codec.encodeStart(ops, (V2)value);
			}
		};
	}

	public static <A> Codec<Optional<A>> optional(Codec<A> codec) {
		return new Codec<Optional<A>>() {
			@Override
			public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> ops, T input) {
				return isEmpty(ops, input) ? DataResult.success(Pair.of(Optional.empty(), input)) : codec.decode(ops, input).map(pair -> pair.mapFirst(Optional::of));
			}

			private static <T> boolean isEmpty(DynamicOps<T> ops, T input) {
				Optional<MapLike<T>> optional = ops.getMap(input).result();
				return optional.isPresent() && ((MapLike)optional.get()).entries().findAny().isEmpty();
			}

			public <T> DataResult<T> encode(Optional<A> optional, DynamicOps<T> dynamicOps, T object) {
				return optional.isEmpty() ? DataResult.success(dynamicOps.emptyMap()) : codec.encode((A)optional.get(), dynamicOps, object);
			}
		};
	}

	public static record StrictUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements Codec<Map<K, V>>, BaseMapCodec<K, V> {
		@Override
		public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
			Builder<K, V> builder = ImmutableMap.builder();

			for (Pair<T, T> pair : input.entries().toList()) {
				DataResult<K> dataResult = this.keyCodec().parse(ops, pair.getFirst());
				DataResult<V> dataResult2 = this.elementCodec().parse(ops, pair.getSecond());
				DataResult<Pair<K, V>> dataResult3 = dataResult.apply2stable(Pair::of, dataResult2);
				Optional<Error<Pair<K, V>>> optional = dataResult3.error();
				if (optional.isPresent()) {
					String string = ((Error)optional.get()).message();
					return DataResult.error(() -> dataResult.result().isPresent() ? "Map entry '" + dataResult.result().get() + "' : " + string : string);
				}

				if (!dataResult3.result().isPresent()) {
					return DataResult.error(() -> "Empty or invalid map contents are not allowed");
				}

				Pair<K, V> pair2 = (Pair<K, V>)dataResult3.result().get();
				builder.put(pair2.getFirst(), pair2.getSecond());
			}

			Map<K, V> map = builder.build();
			return DataResult.success(map);
		}

		@Override
		public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
			return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> this.decode(ops, map)).map(map -> Pair.of(map, input));
		}

		public <T> DataResult<T> encode(Map<K, V> map, DynamicOps<T> dynamicOps, T object) {
			return this.encode(map, dynamicOps, dynamicOps.mapBuilder()).build(object);
		}

		public String toString() {
			return "StrictUnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + "]";
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
}
