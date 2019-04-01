package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_215 implements class_209 {
	private final class_2048 field_1293;
	private final class_47.class_50 field_1292;

	private class_215(class_2048 arg, class_47.class_50 arg2) {
		this.field_1293 = arg;
		this.field_1292 = arg2;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1232, this.field_1292.method_315());
	}

	public boolean method_914(class_47 arg) {
		class_1297 lv = arg.method_296(this.field_1292.method_315());
		class_2338 lv2 = arg.method_296(class_181.field_1232);
		return lv2 != null && this.field_1293.method_8909(arg.method_299(), new class_243(lv2), lv);
	}

	public static class_209.class_210 method_15972(class_47.class_50 arg) {
		return method_917(arg, class_2048.class_2049.method_8916());
	}

	public static class_209.class_210 method_917(class_47.class_50 arg, class_2048.class_2049 arg2) {
		return () -> new class_215(arg2.method_8920(), arg);
	}

	public static class class_216 extends class_209.class_211<class_215> {
		protected class_216() {
			super(new class_2960("entity_properties"), class_215.class);
		}

		public void method_919(JsonObject jsonObject, class_215 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", arg.field_1293.method_8912());
			jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_1292));
		}

		public class_215 method_920(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2048 lv = class_2048.method_8913(jsonObject.get("predicate"));
			return new class_215(lv, class_3518.method_15272(jsonObject, "entity", jsonDeserializationContext, class_47.class_50.class));
		}
	}
}
