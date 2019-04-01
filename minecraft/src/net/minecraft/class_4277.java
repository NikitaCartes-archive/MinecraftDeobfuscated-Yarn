package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Set;

public class class_4277 extends class_120 {
	public class_4277(class_209[] args) {
		super(args);
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of();
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		class_1842 lv = class_2378.field_11143.method_10240(arg2.method_294());
		return class_1844.method_8061(arg, lv);
	}

	public static class_120.class_121<?> method_20236() {
		return method_520(class_4277::new);
	}

	public static class class_4278 extends class_120.class_123<class_4277> {
		public class_4278() {
			super(new class_2960("apply_random_effect"), class_4277.class);
		}

		public class_4277 method_20237(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			return new class_4277(args);
		}
	}
}
