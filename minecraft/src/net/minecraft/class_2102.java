package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class class_2102 {
	public static final class_2102 field_9709 = new class_2102(Collections.emptyMap());
	private final Map<class_1291, class_2102.class_2103> field_9710;

	public class_2102(Map<class_1291, class_2102.class_2103> map) {
		this.field_9710 = map;
	}

	public static class_2102 method_9066() {
		return new class_2102(Maps.<class_1291, class_2102.class_2103>newHashMap());
	}

	public class_2102 method_9065(class_1291 arg) {
		this.field_9710.put(arg, new class_2102.class_2103());
		return this;
	}

	public boolean method_9062(class_1297 arg) {
		if (this == field_9709) {
			return true;
		} else {
			return arg instanceof class_1309 ? this.method_9063(((class_1309)arg).method_6088()) : false;
		}
	}

	public boolean method_9067(class_1309 arg) {
		return this == field_9709 ? true : this.method_9063(arg.method_6088());
	}

	public boolean method_9063(Map<class_1291, class_1293> map) {
		if (this == field_9709) {
			return true;
		} else {
			for (Entry<class_1291, class_2102.class_2103> entry : this.field_9710.entrySet()) {
				class_1293 lv = (class_1293)map.get(entry.getKey());
				if (!((class_2102.class_2103)entry.getValue()).method_9069(lv)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class_2102 method_9064(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "effects");
			Map<class_1291, class_2102.class_2103> map = Maps.<class_1291, class_2102.class_2103>newHashMap();

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				class_2960 lv = new class_2960((String)entry.getKey());
				class_1291 lv2 = class_2378.field_11159.method_10223(lv);
				if (lv2 == null) {
					throw new JsonSyntaxException("Unknown effect '" + lv + "'");
				}

				class_2102.class_2103 lv3 = class_2102.class_2103.method_9070(class_3518.method_15295((JsonElement)entry.getValue(), (String)entry.getKey()));
				map.put(lv2, lv3);
			}

			return new class_2102(map);
		} else {
			return field_9709;
		}
	}

	public JsonElement method_9068() {
		if (this == field_9709) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();

			for (Entry<class_1291, class_2102.class_2103> entry : this.field_9710.entrySet()) {
				jsonObject.add(class_2378.field_11159.method_10221((class_1291)entry.getKey()).toString(), ((class_2102.class_2103)entry.getValue()).method_9071());
			}

			return jsonObject;
		}
	}

	public static class class_2103 {
		private final class_2096.class_2100 field_9711;
		private final class_2096.class_2100 field_9713;
		@Nullable
		private final Boolean field_9714;
		@Nullable
		private final Boolean field_9712;

		public class_2103(class_2096.class_2100 arg, class_2096.class_2100 arg2, @Nullable Boolean boolean_, @Nullable Boolean boolean2) {
			this.field_9711 = arg;
			this.field_9713 = arg2;
			this.field_9714 = boolean_;
			this.field_9712 = boolean2;
		}

		public class_2103() {
			this(class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, null, null);
		}

		public boolean method_9069(@Nullable class_1293 arg) {
			if (arg == null) {
				return false;
			} else if (!this.field_9711.method_9054(arg.method_5578())) {
				return false;
			} else if (!this.field_9713.method_9054(arg.method_5584())) {
				return false;
			} else {
				return this.field_9714 != null && this.field_9714 != arg.method_5591() ? false : this.field_9712 == null || this.field_9712 == arg.method_5581();
			}
		}

		public JsonElement method_9071() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("amplifier", this.field_9711.method_9036());
			jsonObject.add("duration", this.field_9713.method_9036());
			jsonObject.addProperty("ambient", this.field_9714);
			jsonObject.addProperty("visible", this.field_9712);
			return jsonObject;
		}

		public static class_2102.class_2103 method_9070(JsonObject jsonObject) {
			class_2096.class_2100 lv = class_2096.class_2100.method_9056(jsonObject.get("amplifier"));
			class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject.get("duration"));
			Boolean boolean_ = jsonObject.has("ambient") ? class_3518.method_15270(jsonObject, "ambient") : null;
			Boolean boolean2 = jsonObject.has("visible") ? class_3518.method_15270(jsonObject, "visible") : null;
			return new class_2102.class_2103(lv, lv2, boolean_, boolean2);
		}
	}
}
