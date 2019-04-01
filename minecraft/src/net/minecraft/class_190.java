package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_190 implements class_209 {
	private final class_2022 field_1256;

	private class_190(class_2022 arg) {
		this.field_1256 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1232, class_181.field_1231);
	}

	public boolean method_834(class_47 arg) {
		class_1282 lv = arg.method_296(class_181.field_1231);
		class_2338 lv2 = arg.method_296(class_181.field_1232);
		return lv2 != null && lv != null && this.field_1256.method_8845(arg.method_299(), new class_243(lv2), lv);
	}

	public static class_209.class_210 method_837(class_2022.class_2023 arg) {
		return () -> new class_190(arg.method_8851());
	}

	public static class class_191 extends class_209.class_211<class_190> {
		protected class_191() {
			super(new class_2960("damage_source_properties"), class_190.class);
		}

		public void method_838(JsonObject jsonObject, class_190 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", arg.field_1256.method_8848());
		}

		public class_190 method_839(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2022 lv = class_2022.method_8846(jsonObject.get("predicate"));
			return new class_190(lv);
		}
	}
}
