package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_101 extends class_120 {
	private final class_101.class_102 field_1018;

	private class_101(class_209[] args, class_101.class_102 arg) {
		super(args);
		this.field_1018 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(this.field_1018.field_1024);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		Object object = arg2.method_296(this.field_1018.field_1024);
		if (object instanceof class_1275) {
			class_1275 lv = (class_1275)object;
			if (lv.method_16914()) {
				arg.method_7977(lv.method_5476());
			}
		}

		return arg;
	}

	public static class_120.class_121<?> method_473(class_101.class_102 arg) {
		return method_520(args -> new class_101(args, arg));
	}

	public static enum class_102 {
		field_1022("this", class_181.field_1226),
		field_1019("killer", class_181.field_1230),
		field_1020("killer_player", class_181.field_1233),
		field_1023("block_entity", class_181.field_1228);

		public final String field_1025;
		public final class_169<?> field_1024;

		private class_102(String string2, class_169<?> arg) {
			this.field_1025 = string2;
			this.field_1024 = arg;
		}

		public static class_101.class_102 method_475(String string) {
			for (class_101.class_102 lv : values()) {
				if (lv.field_1025.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid name source " + string);
		}
	}

	public static class class_103 extends class_120.class_123<class_101> {
		public class_103() {
			super(new class_2960("copy_name"), class_101.class);
		}

		public void method_476(JsonObject jsonObject, class_101 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("source", arg.field_1018.field_1025);
		}

		public class_101 method_477(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_101.class_102 lv = class_101.class_102.method_475(class_3518.method_15265(jsonObject, "source"));
			return new class_101(args, lv);
		}
	}
}
