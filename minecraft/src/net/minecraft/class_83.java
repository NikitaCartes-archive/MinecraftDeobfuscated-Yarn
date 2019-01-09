package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class class_83 extends class_85 {
	private final class_2960 field_993;

	private class_83(class_2960 arg, int i, int j, class_209[] args, class_117[] args2) {
		super(i, j, args, args2);
		this.field_993 = arg;
	}

	@Override
	public void method_433(Consumer<class_1799> consumer, class_47 arg) {
		class_52 lv = arg.method_301().method_367(this.field_993);
		lv.method_328(arg, consumer);
	}

	@Override
	public void method_415(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		if (set.contains(this.field_993)) {
			arg.method_360("Table " + this.field_993 + " is recursively called");
		} else {
			super.method_415(arg, function, set, arg2);
			class_52 lv = (class_52)function.apply(this.field_993);
			if (lv == null) {
				arg.method_360("Unknown loot table called " + this.field_993);
			} else {
				Set<class_2960> set2 = ImmutableSet.<class_2960>builder().addAll(set).add(this.field_993).build();
				lv.method_330(arg.method_364("->{" + this.field_993 + "}"), function, set2, arg2);
			}
		}
	}

	public static class_85.class_86<?> method_428(class_2960 arg) {
		return method_434((i, j, args, args2) -> new class_83(arg, i, j, args, args2));
	}

	public static class class_84 extends class_85.class_90<class_83> {
		public class_84() {
			super(new class_2960("loot_table"), class_83.class);
		}

		public void method_431(JsonObject jsonObject, class_83 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("name", arg.field_993.toString());
		}

		protected class_83 method_432(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "name"));
			return new class_83(lv, i, j, args, args2);
		}
	}
}
