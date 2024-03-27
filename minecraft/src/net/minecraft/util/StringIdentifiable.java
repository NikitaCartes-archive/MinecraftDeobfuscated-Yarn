package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.Codecs;

/**
 * An interface, usually implemented by enums, that allows the object to be serialized
 * by codecs. An instance is identified using a string.
 * 
 * @apiNote To make an enum serializable with codecs, implement this on the enum class,
 * implement {@link #asString} to return a unique ID, and add a {@code static final}
 * field that holds {@linkplain #createCodec the codec for the enum}.
 */
public interface StringIdentifiable {
	int CACHED_MAP_THRESHOLD = 16;

	/**
	 * {@return the unique string representation of the enum, used for serialization}
	 */
	String asString();

	/**
	 * Creates a codec that serializes an enum implementing this interface either
	 * using its ordinals (when compressed) or using its {@link #asString()} method
	 * and a given decode function.
	 */
	static <E extends Enum<E> & StringIdentifiable> StringIdentifiable.EnumCodec<E> createCodec(Supplier<E[]> enumValues) {
		return createCodec(enumValues, id -> id);
	}

	/**
	 * Creates a codec that serializes an enum implementing this interface either
	 * using its ordinals (when compressed) or using its {@link #asString()} method
	 * and a given decode function.
	 */
	static <E extends Enum<E> & StringIdentifiable> StringIdentifiable.EnumCodec<E> createCodec(
		Supplier<E[]> enumValues, Function<String, String> valueNameTransformer
	) {
		E[] enums = (E[])enumValues.get();
		Function<String, E> function = createMapper(enums, valueNameTransformer);
		return new StringIdentifiable.EnumCodec<>(enums, function);
	}

	static <T extends StringIdentifiable> Codec<T> createBasicCodec(Supplier<T[]> values) {
		T[] stringIdentifiables = (T[])values.get();
		Function<String, T> function = createMapper(stringIdentifiables, valueName -> valueName);
		ToIntFunction<T> toIntFunction = Util.lastIndexGetter(Arrays.asList(stringIdentifiables));
		return new StringIdentifiable.BasicCodec<>(stringIdentifiables, function, toIntFunction);
	}

	static <T extends StringIdentifiable> Function<String, T> createMapper(T[] values, Function<String, String> valueNameTransformer) {
		if (values.length > 16) {
			Map<String, T> map = (Map<String, T>)Arrays.stream(values)
				.collect(Collectors.toMap(value -> (String)valueNameTransformer.apply(value.asString()), value -> value));
			return name -> name == null ? null : (StringIdentifiable)map.get(name);
		} else {
			return name -> {
				for (T stringIdentifiable : values) {
					if (((String)valueNameTransformer.apply(stringIdentifiable.asString())).equals(name)) {
						return stringIdentifiable;
					}
				}

				return null;
			};
		}
	}

	static Keyable toKeyable(StringIdentifiable[] values) {
		return new Keyable() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Arrays.stream(values).map(StringIdentifiable::asString).map(ops::createString);
			}
		};
	}

	public static class BasicCodec<S extends StringIdentifiable> implements Codec<S> {
		private final Codec<S> codec;

		public BasicCodec(S[] values, Function<String, S> idToIdentifiable, ToIntFunction<S> identifiableToOrdinal) {
			this.codec = Codecs.orCompressed(
				Codec.stringResolver(StringIdentifiable::asString, idToIdentifiable),
				Codecs.rawIdChecked(identifiableToOrdinal, ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1)
			);
		}

		@Override
		public <T> DataResult<com.mojang.datafixers.util.Pair<S, T>> decode(DynamicOps<T> ops, T input) {
			return this.codec.decode(ops, input);
		}

		public <T> DataResult<T> encode(S stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
			return this.codec.encode(stringIdentifiable, dynamicOps, object);
		}
	}

	@Deprecated
	public static class EnumCodec<E extends Enum<E> & StringIdentifiable> extends StringIdentifiable.BasicCodec<E> {
		private final Function<String, E> idToIdentifiable;

		public EnumCodec(E[] values, Function<String, E> idToIdentifiable) {
			super(values, idToIdentifiable, enum_ -> ((Enum)enum_).ordinal());
			this.idToIdentifiable = idToIdentifiable;
		}

		@Nullable
		public E byId(@Nullable String id) {
			return (E)this.idToIdentifiable.apply(id);
		}

		public E byId(@Nullable String id, E fallback) {
			return (E)Objects.requireNonNullElse(this.byId(id), fallback);
		}
	}
}
