package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class class_1081 implements class_3270<class_1079> {
	public class_1079 method_4692(JsonObject jsonObject) {
		List<class_1080> list = Lists.<class_1080>newArrayList();
		int i = class_3518.method_15282(jsonObject, "frametime", 1);
		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
		}

		if (jsonObject.has("frames")) {
			try {
				JsonArray jsonArray = class_3518.method_15261(jsonObject, "frames");

				for (int j = 0; j < jsonArray.size(); j++) {
					JsonElement jsonElement = jsonArray.get(j);
					class_1080 lv = this.method_4693(j, jsonElement);
					if (lv != null) {
						list.add(lv);
					}
				}
			} catch (ClassCastException var8) {
				throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), var8);
			}
		}

		int k = class_3518.method_15282(jsonObject, "width", -1);
		int jx = class_3518.method_15282(jsonObject, "height", -1);
		if (k != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
		}

		if (jx != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long)jx, "Invalid height");
		}

		boolean bl = class_3518.method_15258(jsonObject, "interpolate", false);
		return new class_1079(list, k, jx, i, bl);
	}

	private class_1080 method_4693(int i, JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive()) {
			return new class_1080(class_3518.method_15257(jsonElement, "frames[" + i + "]"));
		} else if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "frames[" + i + "]");
			int j = class_3518.method_15282(jsonObject, "time", -1);
			if (jsonObject.has("time")) {
				Validate.inclusiveBetween(1L, 2147483647L, (long)j, "Invalid frame time");
			}

			int k = class_3518.method_15260(jsonObject, "index");
			Validate.inclusiveBetween(0L, 2147483647L, (long)k, "Invalid frame index");
			return new class_1080(k, j);
		} else {
			return null;
		}
	}

	@Override
	public String method_14420() {
		return "animation";
	}
}
