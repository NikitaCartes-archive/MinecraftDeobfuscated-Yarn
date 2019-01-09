package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class class_159 extends class_120 {
	private final class_2487 field_1138;

	private class_159(class_209[] args, class_2487 arg) {
		super(args);
		this.field_1138 = arg;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		arg.method_7948().method_10543(this.field_1138);
		return arg;
	}

	public static class_120.class_121<?> method_677(class_2487 arg) {
		return method_520(args -> new class_159(args, arg));
	}

	public static class class_160 extends class_120.class_123<class_159> {
		public class_160() {
			super(new class_2960("set_nbt"), class_159.class);
		}

		public void method_678(JsonObject jsonObject, class_159 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("tag", arg.field_1138.toString());
		}

		public class_159 method_679(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			try {
				class_2487 lv = class_2522.method_10718(class_3518.method_15265(jsonObject, "tag"));
				return new class_159(args, lv);
			} catch (CommandSyntaxException var5) {
				throw new JsonSyntaxException(var5.getMessage());
			}
		}
	}
}
