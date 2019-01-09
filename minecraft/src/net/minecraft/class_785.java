package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_785 {
	public final class_1160 field_4228;
	public final class_1160 field_4231;
	public final Map<class_2350, class_783> field_4230;
	public final class_789 field_4232;
	public final boolean field_4229;

	public class_785(class_1160 arg, class_1160 arg2, Map<class_2350, class_783> map, @Nullable class_789 arg3, boolean bl) {
		this.field_4228 = arg;
		this.field_4231 = arg2;
		this.field_4230 = map;
		this.field_4232 = arg3;
		this.field_4229 = bl;
		this.method_3402();
	}

	private void method_3402() {
		for (Entry<class_2350, class_783> entry : this.field_4230.entrySet()) {
			float[] fs = this.method_3401((class_2350)entry.getKey());
			((class_783)entry.getValue()).field_4227.method_3417(fs);
		}
	}

	private float[] method_3401(class_2350 arg) {
		switch (arg) {
			case field_11033:
				return new float[]{
					this.field_4228.method_4943(), 16.0F - this.field_4231.method_4947(), this.field_4231.method_4943(), 16.0F - this.field_4228.method_4947()
				};
			case field_11036:
				return new float[]{this.field_4228.method_4943(), this.field_4228.method_4947(), this.field_4231.method_4943(), this.field_4231.method_4947()};
			case field_11043:
			default:
				return new float[]{
					16.0F - this.field_4231.method_4943(), 16.0F - this.field_4231.method_4945(), 16.0F - this.field_4228.method_4943(), 16.0F - this.field_4228.method_4945()
				};
			case field_11035:
				return new float[]{
					this.field_4228.method_4943(), 16.0F - this.field_4231.method_4945(), this.field_4231.method_4943(), 16.0F - this.field_4228.method_4945()
				};
			case field_11039:
				return new float[]{
					this.field_4228.method_4947(), 16.0F - this.field_4231.method_4945(), this.field_4231.method_4947(), 16.0F - this.field_4228.method_4945()
				};
			case field_11034:
				return new float[]{
					16.0F - this.field_4231.method_4947(), 16.0F - this.field_4231.method_4945(), 16.0F - this.field_4228.method_4947(), 16.0F - this.field_4228.method_4945()
				};
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_786 implements JsonDeserializer<class_785> {
		protected class_786() {
		}

		public class_785 method_3406(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_1160 lv = this.method_3407(jsonObject);
			class_1160 lv2 = this.method_3405(jsonObject);
			class_789 lv3 = this.method_3410(jsonObject);
			Map<class_2350, class_783> map = this.method_3412(jsonDeserializationContext, jsonObject);
			if (jsonObject.has("shade") && !class_3518.method_15254(jsonObject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			} else {
				boolean bl = class_3518.method_15258(jsonObject, "shade", true);
				return new class_785(lv, lv2, map, lv3, bl);
			}
		}

		@Nullable
		private class_789 method_3410(JsonObject jsonObject) {
			class_789 lv = null;
			if (jsonObject.has("rotation")) {
				JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "rotation");
				class_1160 lv2 = this.method_3409(jsonObject2, "origin");
				lv2.method_4942(0.0625F);
				class_2350.class_2351 lv3 = this.method_3411(jsonObject2);
				float f = this.method_3403(jsonObject2);
				boolean bl = class_3518.method_15258(jsonObject2, "rescale", false);
				lv = new class_789(lv2, lv3, f, bl);
			}

			return lv;
		}

		private float method_3403(JsonObject jsonObject) {
			float f = class_3518.method_15259(jsonObject, "angle");
			if (f != 0.0F && class_3532.method_15379(f) != 22.5F && class_3532.method_15379(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private class_2350.class_2351 method_3411(JsonObject jsonObject) {
			String string = class_3518.method_15265(jsonObject, "axis");
			class_2350.class_2351 lv = class_2350.class_2351.method_10177(string.toLowerCase(Locale.ROOT));
			if (lv == null) {
				throw new JsonParseException("Invalid rotation axis: " + string);
			} else {
				return lv;
			}
		}

		private Map<class_2350, class_783> method_3412(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<class_2350, class_783> map = this.method_3404(jsonDeserializationContext, jsonObject);
			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<class_2350, class_783> method_3404(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<class_2350, class_783> map = Maps.newEnumMap(class_2350.class);
			JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "faces");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				class_2350 lv = this.method_3408((String)entry.getKey());
				map.put(lv, jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), class_783.class));
			}

			return map;
		}

		private class_2350 method_3408(String string) {
			class_2350 lv = class_2350.method_10168(string);
			if (lv == null) {
				throw new JsonParseException("Unknown facing: " + string);
			} else {
				return lv;
			}
		}

		private class_1160 method_3405(JsonObject jsonObject) {
			class_1160 lv = this.method_3409(jsonObject, "to");
			if (!(lv.method_4943() < -16.0F)
				&& !(lv.method_4945() < -16.0F)
				&& !(lv.method_4947() < -16.0F)
				&& !(lv.method_4943() > 32.0F)
				&& !(lv.method_4945() > 32.0F)
				&& !(lv.method_4947() > 32.0F)) {
				return lv;
			} else {
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + lv);
			}
		}

		private class_1160 method_3407(JsonObject jsonObject) {
			class_1160 lv = this.method_3409(jsonObject, "from");
			if (!(lv.method_4943() < -16.0F)
				&& !(lv.method_4945() < -16.0F)
				&& !(lv.method_4947() < -16.0F)
				&& !(lv.method_4943() > 32.0F)
				&& !(lv.method_4945() > 32.0F)
				&& !(lv.method_4947() > 32.0F)) {
				return lv;
			} else {
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + lv);
			}
		}

		private class_1160 method_3409(JsonObject jsonObject, String string) {
			JsonArray jsonArray = class_3518.method_15261(jsonObject, string);
			if (jsonArray.size() != 3) {
				throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
			} else {
				float[] fs = new float[3];

				for (int i = 0; i < fs.length; i++) {
					fs[i] = class_3518.method_15269(jsonArray.get(i), string + "[" + i + "]");
				}

				return new class_1160(fs[0], fs[1], fs[2]);
			}
		}
	}
}
