package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;

public class class_77 extends class_85 {
	private final class_1792 field_987;

	private class_77(class_1792 arg, int i, int j, class_209[] args, class_117[] args2) {
		super(i, j, args, args2);
		this.field_987 = arg;
	}

	@Override
	public void method_433(Consumer<class_1799> consumer, class_47 arg) {
		consumer.accept(new class_1799(this.field_987));
	}

	public static class_85.class_86<?> method_411(class_1935 arg) {
		return method_434((i, j, args, args2) -> new class_77(arg.method_8389(), i, j, args, args2));
	}

	public static class class_78 extends class_85.class_90<class_77> {
		public class_78() {
			super(new class_2960("item"), class_77.class);
		}

		public void method_412(JsonObject jsonObject, class_77 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, arg, jsonSerializationContext);
			class_2960 lv = class_2378.field_11142.method_10221(arg.field_987);
			if (lv == null) {
				throw new IllegalArgumentException("Can't serialize unknown item " + arg.field_987);
			} else {
				jsonObject.addProperty("name", lv.toString());
			}
		}

		protected class_77 method_413(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2) {
			class_1792 lv = class_3518.method_15288(jsonObject, "name");
			return new class_77(lv, i, j, args, args2);
		}
	}
}
