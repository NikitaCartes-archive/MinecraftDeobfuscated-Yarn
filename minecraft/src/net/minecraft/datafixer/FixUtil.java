package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

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

	public static <T, R> Typed<R> withType(Type<R> type, Typed<T> typed) {
		return new Typed<>(type, typed.getOps(), (R)typed.getValue());
	}

	public static Type<?> withTypeChanged(Type<?> type, Type<?> oldType, Type<?> newType) {
		return type.all(typeChangingRule(oldType, newType), true, false).view().newType();
	}

	private static <A, B> TypeRewriteRule typeChangingRule(Type<A> oldType, Type<B> newType) {
		RewriteResult<A, B> rewriteResult = RewriteResult.create(View.create("Patcher", oldType, newType, dynamicOps -> object -> {
				throw new UnsupportedOperationException();
			}), new BitSet());
		return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(oldType, rewriteResult), PointFreeRule.nop(), true, true);
	}

	@SafeVarargs
	public static <T> Function<Typed<?>, Typed<?>> compose(Function<Typed<?>, Typed<?>>... fixes) {
		return typed -> {
			for (Function<Typed<?>, Typed<?>> function : fixes) {
				typed = (Typed)function.apply(typed);
			}

			return typed;
		};
	}

	public static Dynamic<?> createBlockState(String id, Map<String, String> properties) {
		Dynamic<NbtElement> dynamic = new Dynamic<>(NbtOps.INSTANCE, new NbtCompound());
		Dynamic<NbtElement> dynamic2 = dynamic.set("Name", dynamic.createString(id));
		if (!properties.isEmpty()) {
			dynamic2 = dynamic2.set(
				"Properties",
				dynamic.createMap(
					(Map<? extends Dynamic<?>, ? extends Dynamic<?>>)properties.entrySet()
						.stream()
						.collect(Collectors.toMap(entry -> dynamic.createString((String)entry.getKey()), entry -> dynamic.createString((String)entry.getValue())))
				)
			);
		}

		return dynamic2;
	}

	public static Dynamic<?> createBlockState(String id) {
		return createBlockState(id, Map.of());
	}

	public static Dynamic<?> apply(Dynamic<?> dynamic, String fieldName, UnaryOperator<String> applier) {
		return dynamic.update(fieldName, value -> DataFixUtils.orElse(value.asString().map(applier).map(dynamic::createString).result(), value));
	}
}
