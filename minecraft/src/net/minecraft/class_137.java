package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_137 extends class_120 {
	private final List<class_137.class_138> field_1105;

	private class_137(class_209[] args, List<class_137.class_138> list) {
		super(args);
		this.field_1105 = ImmutableList.copyOf(list);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		Random random = arg2.method_294();

		for (class_137.class_138 lv : this.field_1105) {
			UUID uUID = lv.field_1111;
			if (uUID == null) {
				uUID = UUID.randomUUID();
			}

			class_1304 lv2 = lv.field_1112[random.nextInt(lv.field_1112.length)];
			arg.method_7916(lv.field_1110, new class_1322(uUID, lv.field_1107, (double)lv.field_1108.method_374(random), lv.field_1109), lv2);
		}

		return arg;
	}

	static class class_138 {
		private final String field_1107;
		private final String field_1110;
		private final class_1322.class_1323 field_1109;
		private final class_61 field_1108;
		@Nullable
		private final UUID field_1111;
		private final class_1304[] field_1112;

		private class_138(String string, String string2, class_1322.class_1323 arg, class_61 arg2, class_1304[] args, @Nullable UUID uUID) {
			this.field_1107 = string;
			this.field_1110 = string2;
			this.field_1109 = arg;
			this.field_1108 = arg2;
			this.field_1111 = uUID;
			this.field_1112 = args;
		}

		public JsonObject method_615(JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", this.field_1107);
			jsonObject.addProperty("attribute", this.field_1110);
			jsonObject.addProperty("operation", method_612(this.field_1109));
			jsonObject.add("amount", jsonSerializationContext.serialize(this.field_1108));
			if (this.field_1111 != null) {
				jsonObject.addProperty("id", this.field_1111.toString());
			}

			if (this.field_1112.length == 1) {
				jsonObject.addProperty("slot", this.field_1112[0].method_5923());
			} else {
				JsonArray jsonArray = new JsonArray();

				for (class_1304 lv : this.field_1112) {
					jsonArray.add(new JsonPrimitive(lv.method_5923()));
				}

				jsonObject.add("slot", jsonArray);
			}

			return jsonObject;
		}

		public static class_137.class_138 method_614(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = class_3518.method_15265(jsonObject, "name");
			String string2 = class_3518.method_15265(jsonObject, "attribute");
			class_1322.class_1323 lv = method_609(class_3518.method_15265(jsonObject, "operation"));
			class_61 lv2 = class_3518.method_15272(jsonObject, "amount", jsonDeserializationContext, class_61.class);
			UUID uUID = null;
			class_1304[] lvs;
			if (class_3518.method_15289(jsonObject, "slot")) {
				lvs = new class_1304[]{class_1304.method_5924(class_3518.method_15265(jsonObject, "slot"))};
			} else {
				if (!class_3518.method_15264(jsonObject, "slot")) {
					throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
				}

				JsonArray jsonArray = class_3518.method_15261(jsonObject, "slot");
				lvs = new class_1304[jsonArray.size()];
				int i = 0;

				for (JsonElement jsonElement : jsonArray) {
					lvs[i++] = class_1304.method_5924(class_3518.method_15287(jsonElement, "slot"));
				}

				if (lvs.length == 0) {
					throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
				}
			}

			if (jsonObject.has("id")) {
				String string3 = class_3518.method_15265(jsonObject, "id");

				try {
					uUID = UUID.fromString(string3);
				} catch (IllegalArgumentException var12) {
					throw new JsonSyntaxException("Invalid attribute modifier id '" + string3 + "' (must be UUID format, with dashes)");
				}
			}

			return new class_137.class_138(string, string2, lv, lv2, lvs, uUID);
		}

		private static String method_612(class_1322.class_1323 arg) {
			switch (arg) {
				case field_6328:
					return "addition";
				case field_6330:
					return "multiply_base";
				case field_6331:
					return "multiply_total";
				default:
					throw new IllegalArgumentException("Unknown operation " + arg);
			}
		}

		private static class_1322.class_1323 method_609(String string) {
			switch (string) {
				case "addition":
					return class_1322.class_1323.field_6328;
				case "multiply_base":
					return class_1322.class_1323.field_6330;
				case "multiply_total":
					return class_1322.class_1323.field_6331;
				default:
					throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
			}
		}
	}

	public static class class_139 extends class_120.class_123<class_137> {
		public class_139() {
			super(new class_2960("set_attributes"), class_137.class);
		}

		public void method_618(JsonObject jsonObject, class_137 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			JsonArray jsonArray = new JsonArray();

			for (class_137.class_138 lv : arg.field_1105) {
				jsonArray.add(lv.method_615(jsonSerializationContext));
			}

			jsonObject.add("modifiers", jsonArray);
		}

		public class_137 method_617(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			JsonArray jsonArray = class_3518.method_15261(jsonObject, "modifiers");
			List<class_137.class_138> list = Lists.<class_137.class_138>newArrayListWithExpectedSize(jsonArray.size());

			for (JsonElement jsonElement : jsonArray) {
				list.add(class_137.class_138.method_614(class_3518.method_15295(jsonElement, "modifier"), jsonDeserializationContext));
			}

			if (list.isEmpty()) {
				throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
			} else {
				return new class_137(args, list);
			}
		}
	}
}
