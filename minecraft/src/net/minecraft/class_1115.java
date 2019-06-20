package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class class_1115 implements JsonDeserializer<class_1110> {
	public class_1110 method_4791(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "entry");
		boolean bl = class_3518.method_15258(jsonObject, "replace", false);
		String string = class_3518.method_15253(jsonObject, "subtitle", null);
		List<class_1111> list = this.method_4792(jsonObject);
		return new class_1110(list, bl, string);
	}

	private List<class_1111> method_4792(JsonObject jsonObject) {
		List<class_1111> list = Lists.<class_1111>newArrayList();
		if (jsonObject.has("sounds")) {
			JsonArray jsonArray = class_3518.method_15261(jsonObject, "sounds");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				if (class_3518.method_15286(jsonElement)) {
					String string = class_3518.method_15287(jsonElement, "sound");
					list.add(new class_1111(string, 1.0F, 1.0F, 1, class_1111.class_1112.field_5474, false, false, 16));
				} else {
					list.add(this.method_4790(class_3518.method_15295(jsonElement, "sound")));
				}
			}
		}

		return list;
	}

	private class_1111 method_4790(JsonObject jsonObject) {
		String string = class_3518.method_15265(jsonObject, "name");
		class_1111.class_1112 lv = this.method_4789(jsonObject, class_1111.class_1112.field_5474);
		float f = class_3518.method_15277(jsonObject, "volume", 1.0F);
		Validate.isTrue(f > 0.0F, "Invalid volume");
		float g = class_3518.method_15277(jsonObject, "pitch", 1.0F);
		Validate.isTrue(g > 0.0F, "Invalid pitch");
		int i = class_3518.method_15282(jsonObject, "weight", 1);
		Validate.isTrue(i > 0, "Invalid weight");
		boolean bl = class_3518.method_15258(jsonObject, "preload", false);
		boolean bl2 = class_3518.method_15258(jsonObject, "stream", false);
		int j = class_3518.method_15282(jsonObject, "attenuation_distance", 16);
		return new class_1111(string, f, g, i, lv, bl2, bl, j);
	}

	private class_1111.class_1112 method_4789(JsonObject jsonObject, class_1111.class_1112 arg) {
		class_1111.class_1112 lv = arg;
		if (jsonObject.has("type")) {
			lv = class_1111.class_1112.method_4773(class_3518.method_15265(jsonObject, "type"));
			Validate.notNull(lv, "Invalid type");
		}

		return lv;
	}
}
