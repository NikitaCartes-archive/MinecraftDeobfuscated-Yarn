package net.minecraft;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public abstract class class_2050 {
	public static final class_2050 field_9609 = new class_2050() {
		@Override
		public boolean method_8925(class_1299<?> arg) {
			return true;
		}

		@Override
		public JsonElement method_8927() {
			return JsonNull.INSTANCE;
		}
	};
	private static final Joiner field_9608 = Joiner.on(", ");

	public abstract boolean method_8925(class_1299<?> arg);

	public abstract JsonElement method_8927();

	public static class_2050 method_8928(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			String string = class_3518.method_15287(jsonElement, "type");
			if (string.startsWith("#")) {
				class_2960 lv = new class_2960(string.substring(1));
				class_3494<class_1299<?>> lv2 = class_3483.method_15082().method_15188(lv);
				return new class_2050.class_2051(lv2);
			} else {
				class_2960 lv = new class_2960(string);
				class_1299<?> lv3 = (class_1299<?>)class_2378.field_11145
					.method_17966(lv)
					.orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + lv + "', valid types are: " + field_9608.join(class_2378.field_11145.method_10235())));
				return new class_2050.class_2052(lv3);
			}
		} else {
			return field_9609;
		}
	}

	public static class_2050 method_8929(class_1299<?> arg) {
		return new class_2050.class_2052(arg);
	}

	public static class_2050 method_8926(class_3494<class_1299<?>> arg) {
		return new class_2050.class_2051(arg);
	}

	static class class_2051 extends class_2050 {
		private final class_3494<class_1299<?>> field_9610;

		public class_2051(class_3494<class_1299<?>> arg) {
			this.field_9610 = arg;
		}

		@Override
		public boolean method_8925(class_1299<?> arg) {
			return this.field_9610.method_15141(arg);
		}

		@Override
		public JsonElement method_8927() {
			return new JsonPrimitive("#" + this.field_9610.method_15143().toString());
		}
	}

	static class class_2052 extends class_2050 {
		private final class_1299<?> field_9611;

		public class_2052(class_1299<?> arg) {
			this.field_9611 = arg;
		}

		@Override
		public boolean method_8925(class_1299<?> arg) {
			return this.field_9611 == arg;
		}

		@Override
		public JsonElement method_8927() {
			return new JsonPrimitive(class_2378.field_11145.method_10221(this.field_9611).toString());
		}
	}
}
