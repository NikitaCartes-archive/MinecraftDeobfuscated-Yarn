package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_149 extends class_120 {
	private static final Logger field_1121 = LogManager.getLogger();
	private final class_61 field_1120;

	private class_149(class_209[] args, class_61 arg) {
		super(args);
		this.field_1120 = arg;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7963()) {
			float f = 1.0F - this.field_1120.method_374(arg2.method_294());
			arg.method_7974(class_3532.method_15375(f * (float)arg.method_7936()));
		} else {
			field_1121.warn("Couldn't set damage of loot item {}", arg);
		}

		return arg;
	}

	public static class_120.class_121<?> method_633(class_61 arg) {
		return method_520(args -> new class_149(args, arg));
	}

	public static class class_150 extends class_120.class_123<class_149> {
		protected class_150() {
			super(new class_2960("set_damage"), class_149.class);
		}

		public void method_636(JsonObject jsonObject, class_149 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("damage", jsonSerializationContext.serialize(arg.field_1120));
		}

		public class_149 method_635(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			return new class_149(args, class_3518.method_15272(jsonObject, "damage", jsonDeserializationContext, class_61.class));
		}
	}
}
