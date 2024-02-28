package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.List;
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

	public static Dynamic<?> copyValue(Dynamic<?> from, String fromKey, Dynamic<?> to, String toKey) {
		return copyAndTransformValue(from, fromKey, to, toKey, UnaryOperator.identity());
	}

	public static <T> Dynamic<?> copyAndTransformValue(Dynamic<T> from, String fromKey, Dynamic<?> to, String toKey, UnaryOperator<Dynamic<T>> valueTransformer) {
		Optional<Dynamic<T>> optional = from.get(fromKey).result();
		return optional.isPresent() ? to.set(toKey, (Dynamic<?>)valueTransformer.apply((Dynamic)optional.get())) : to;
	}

	@SafeVarargs
	public static TypeTemplate method_57188(Pair<String, TypeTemplate>... pairs) {
		List<TypeTemplate> list = Arrays.stream(pairs).map(pair -> DSL.optional(DSL.field((String)pair.getFirst(), (TypeTemplate)pair.getSecond()))).toList();
		return DSL.allWithRemainder((TypeTemplate)list.get(0), (TypeTemplate[])list.subList(1, list.size()).toArray(new TypeTemplate[0]));
	}

	private static <T> DataResult<Boolean> getBoolean(Dynamic<T> dynamic) {
		return dynamic.getOps().getBooleanValue(dynamic.getValue());
	}

	public static DataResult<Boolean> getBoolean(DynamicLike<?> dynamicLike) {
		if (dynamicLike instanceof Dynamic<?> dynamic) {
			return getBoolean(dynamic);
		} else {
			return dynamicLike instanceof OptionalDynamic<?> optionalDynamic
				? optionalDynamic.get().flatMap(FixUtil::getBoolean)
				: DataResult.error(() -> "Unknown dynamic value: " + dynamicLike);
		}
	}

	public static boolean getBoolean(DynamicLike<?> dynamicLike, boolean fallback) {
		return (Boolean)getBoolean(dynamicLike).result().orElse(fallback);
	}

	public static <T, R> Typed<R> method_57182(Type<R> type, Typed<T> typed) {
		return new Typed<>(type, typed.getOps(), (R)typed.getValue());
	}
}
