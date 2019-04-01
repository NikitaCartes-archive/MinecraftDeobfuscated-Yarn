package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class class_186 implements class_209 {
	private final class_209[] field_1246;
	private final Predicate<class_47> field_1247;

	private class_186(class_209[] args) {
		this.field_1246 = args;
		this.field_1247 = class_217.method_925(args);
	}

	public final boolean method_825(class_47 arg) {
		return this.field_1247.test(arg);
	}

	@Override
	public void method_292(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		class_209.super.method_292(arg, function, set, arg2);

		for (int i = 0; i < this.field_1246.length; i++) {
			this.field_1246[i].method_292(arg.method_364(".term[" + i + "]"), function, set, arg2);
		}
	}

	public static class_186.class_187 method_826(class_209.class_210... args) {
		return new class_186.class_187(args);
	}

	public static class class_187 implements class_209.class_210 {
		private final List<class_209> field_1248 = Lists.<class_209>newArrayList();

		public class_187(class_209.class_210... args) {
			for (class_209.class_210 lv : args) {
				this.field_1248.add(lv.build());
			}
		}

		@Override
		public class_186.class_187 method_893(class_209.class_210 arg) {
			this.field_1248.add(arg.build());
			return this;
		}

		@Override
		public class_209 build() {
			return new class_186((class_209[])this.field_1248.toArray(new class_209[0]));
		}
	}

	public static class class_188 extends class_209.class_211<class_186> {
		public class_188() {
			super(new class_2960("alternative"), class_186.class);
		}

		public void method_828(JsonObject jsonObject, class_186 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("terms", jsonSerializationContext.serialize(arg.field_1246));
		}

		public class_186 method_829(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_209[] lvs = class_3518.method_15272(jsonObject, "terms", jsonDeserializationContext, class_209[].class);
			return new class_186(lvs);
		}
	}
}
