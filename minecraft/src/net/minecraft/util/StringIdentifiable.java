package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;

public interface StringIdentifiable {
	String asString();

	/**
	 * Creates a codec that serializes an enum implementing this interface either
	 * using its ordinals (when compressed) or using its {@link #asString()} method
	 * and a given decode function.
	 */
	static <E extends Enum<E> & StringIdentifiable> Codec<E> createCodec(Supplier<E[]> enumValues, Function<String, E> fromString) {
		E[] enums = (E[])enumValues.get();
		return Codecs.method_39512(
			Codecs.method_39508(object -> ((StringIdentifiable)object).asString(), fromString),
			Codecs.method_39511(object -> ((Enum)object).ordinal(), ordinal -> ordinal >= 0 && ordinal < enums.length ? enums[ordinal] : null, -1)
		);
	}

	static Keyable toKeyable(StringIdentifiable[] values) {
		return new Keyable() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
				return Arrays.stream(values).map(StringIdentifiable::asString).map(dynamicOps::createString);
			}
		};
	}
}
