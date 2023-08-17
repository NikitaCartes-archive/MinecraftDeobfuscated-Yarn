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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.Codecs;

/**
 * An interface, implemented by enums, that allows the enum to be serialized
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
		if (enums.length > 16) {
			Map<String, E> map = (Map<String, E>)Arrays.stream(enums)
				.collect(Collectors.toMap(enum_ -> (String)valueNameTransformer.apply(((StringIdentifiable)enum_).asString()), enum_ -> enum_));
			return new StringIdentifiable.EnumCodec<>(enums, id -> id == null ? null : (Enum)map.get(id));
		} else {
			return new StringIdentifiable.EnumCodec<>(enums, id -> {
				for (E enum_ : enums) {
					if (((String)valueNameTransformer.apply(enum_.asString())).equals(id)) {
						return enum_;
					}
				}

				return null;
			});
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

	@Deprecated
	public static class EnumCodec<E extends Enum<E> & StringIdentifiable> implements Codec<E> {
		private final Codec<E> base;
		private final Function<String, E> idToIdentifiable;

		public EnumCodec(E[] values, Function<String, E> idToIdentifiable) {
			this.base = Codecs.orCompressed(
				Codecs.idChecked(identifiable -> ((StringIdentifiable)identifiable).asString(), idToIdentifiable),
				Codecs.rawIdChecked(enum_ -> ((Enum)enum_).ordinal(), ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1)
			);
			this.idToIdentifiable = idToIdentifiable;
		}

		@Override
		public <T> DataResult<com.mojang.datafixers.util.Pair<E, T>> decode(DynamicOps<T> ops, T input) {
			return this.base.decode(ops, input);
		}

		public <T> DataResult<T> encode(E enum_, DynamicOps<T> dynamicOps, T object) {
			return this.base.encode(enum_, dynamicOps, object);
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
