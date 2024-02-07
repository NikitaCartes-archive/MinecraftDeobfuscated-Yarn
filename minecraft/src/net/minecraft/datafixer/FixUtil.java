package net.minecraft.datafixer;

import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class FixUtil {
	public static Dynamic<?> fixBlockPos(Dynamic<?> dynamic) {
		Optional<Number> optional = dynamic.get("X").asNumber().result();
		Optional<Number> optional2 = dynamic.get("Y").asNumber().result();
		Optional<Number> optional3 = dynamic.get("Z").asNumber().result();
		return !optional.isEmpty() && !optional2.isEmpty() && !optional3.isEmpty()
			? dynamic.createIntList(
				IntStream.of(new int[]{((Number)optional.get()).intValue(), ((Number)optional2.get()).intValue(), ((Number)optional3.get()).intValue()})
			)
			: dynamic;
	}

	public static <T> Dynamic<T> setOptional(Dynamic<T> dynamic, String key, Optional<? extends Dynamic<?>> value) {
		return value.isEmpty() ? dynamic : dynamic.set(key, (Dynamic<?>)value.get());
	}

	public static <T> Dynamic<T> renameKey(Dynamic<T> dynamic, String oldKey, String newKey) {
		return replaceKey(dynamic, oldKey, newKey, UnaryOperator.identity());
	}

	public static <T> Dynamic<T> replaceKey(Dynamic<T> dynamic, String oldKey, String newKey, Optional<? extends Dynamic<?>> newValue) {
		return setOptional(dynamic.remove(oldKey), newKey, newValue);
	}

	public static <T> Dynamic<T> replaceKey(Dynamic<T> dynamic, String oldKey, String newKey, UnaryOperator<Dynamic<?>> valueTransformer) {
		return setOptional(dynamic.remove(oldKey), newKey, dynamic.get(oldKey).result().map(valueTransformer));
	}
}
