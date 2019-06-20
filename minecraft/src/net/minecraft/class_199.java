package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class class_199 implements class_209 {
	private final Map<String, class_61> field_1279;
	private final class_47.class_50 field_1278;

	private class_199(Map<String, class_61> map, class_47.class_50 arg) {
		this.field_1279 = ImmutableMap.copyOf(map);
		this.field_1278 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(this.field_1278.method_315());
	}

	public boolean method_864(class_47 arg) {
		class_1297 lv = arg.method_296(this.field_1278.method_315());
		if (lv == null) {
			return false;
		} else {
			class_269 lv2 = lv.field_6002.method_8428();

			for (Entry<String, class_61> entry : this.field_1279.entrySet()) {
				if (!this.method_865(lv, lv2, (String)entry.getKey(), (class_61)entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean method_865(class_1297 arg, class_269 arg2, String string, class_61 arg3) {
		class_266 lv = arg2.method_1170(string);
		if (lv == null) {
			return false;
		} else {
			String string2 = arg.method_5820();
			return !arg2.method_1183(string2, lv) ? false : arg3.method_376(arg2.method_1180(string2, lv).method_1126());
		}
	}

	public static class class_200 extends class_209.class_211<class_199> {
		protected class_200() {
			super(new class_2960("entity_scores"), class_199.class);
		}

		public void method_868(JsonObject jsonObject, class_199 arg, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, class_61> entry : arg.field_1279.entrySet()) {
				jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
			}

			jsonObject.add("scores", jsonObject2);
			jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_1278));
		}

		public class_199 method_867(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Set<Entry<String, JsonElement>> set = class_3518.method_15296(jsonObject, "scores").entrySet();
			Map<String, class_61> map = Maps.<String, class_61>newLinkedHashMap();

			for (Entry<String, JsonElement> entry : set) {
				map.put(entry.getKey(), class_3518.method_15291((JsonElement)entry.getValue(), "score", jsonDeserializationContext, class_61.class));
			}

			return new class_199(map, class_3518.method_15272(jsonObject, "entity", jsonDeserializationContext, class_47.class_50.class));
		}
	}
}
