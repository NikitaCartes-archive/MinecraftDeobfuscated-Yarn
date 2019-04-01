package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class class_3124 implements class_3037 {
	public final class_3124.class_3125 field_13725;
	public final int field_13723;
	public final class_2680 field_13724;

	public class_3124(class_3124.class_3125 arg, class_2680 arg2, int i) {
		this.field_13723 = i;
		this.field_13724 = arg2;
		this.field_13725 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("size"),
					dynamicOps.createInt(this.field_13723),
					dynamicOps.createString("target"),
					dynamicOps.createString(this.field_13725.method_13635()),
					dynamicOps.createString("state"),
					class_2680.method_16550(dynamicOps, this.field_13724).getValue()
				)
			)
		);
	}

	public static class_3124 method_13634(Dynamic<?> dynamic) {
		int i = dynamic.get("size").asInt(0);
		class_3124.class_3125 lv = class_3124.class_3125.method_13638(dynamic.get("target").asString(""));
		class_2680 lv2 = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		return new class_3124(lv, lv2, i);
	}

	public static enum class_3125 {
		field_13730("natural_stone", arg -> {
			if (arg == null) {
				return false;
			} else {
				class_2248 lv = arg.method_11614();
				return lv == class_2246.field_10340 || lv == class_2246.field_10474 || lv == class_2246.field_10508 || lv == class_2246.field_10115;
			}
		}),
		field_13727("netherrack", new class_2717(class_2246.field_10515));

		private static final Map<String, class_3124.class_3125> field_13728 = (Map<String, class_3124.class_3125>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3124.class_3125::method_13635, arg -> arg));
		private final String field_13726;
		private final Predicate<class_2680> field_13731;

		private class_3125(String string2, Predicate<class_2680> predicate) {
			this.field_13726 = string2;
			this.field_13731 = predicate;
		}

		public String method_13635() {
			return this.field_13726;
		}

		public static class_3124.class_3125 method_13638(String string) {
			return (class_3124.class_3125)field_13728.get(string);
		}

		public Predicate<class_2680> method_13636() {
			return this.field_13731;
		}
	}
}
