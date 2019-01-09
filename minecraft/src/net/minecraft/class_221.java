package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_221 implements class_209 {
	private static final class_221 field_1297 = new class_221();

	private class_221() {
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1233);
	}

	public boolean method_938(class_47 arg) {
		return arg.method_300(class_181.field_1233);
	}

	public static class_209.class_210 method_939() {
		return () -> field_1297;
	}

	public static class class_222 extends class_209.class_211<class_221> {
		protected class_222() {
			super(new class_2960("killed_by_player"), class_221.class);
		}

		public void method_942(JsonObject jsonObject, class_221 arg, JsonSerializationContext jsonSerializationContext) {
		}

		public class_221 method_943(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return class_221.field_1297;
		}
	}
}
