package net.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import org.apache.commons.lang3.StringUtils;

public class class_3577 extends class_1197 {
	public static final Gson field_15827 = new GsonBuilder().registerTypeAdapter(class_2561.class, new JsonDeserializer<class_2561>() {
		public class_2561 method_15583(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new class_2585(jsonElement.getAsString());
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				class_2561 lv = null;

				for (JsonElement jsonElement2 : jsonArray) {
					class_2561 lv2 = this.method_15583(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
					if (lv == null) {
						lv = lv2;
					} else {
						lv.method_10852(lv2);
					}
				}

				return lv;
			} else {
				throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
			}
		}
	}).create();

	public class_3577(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntitySignTextStrictJsonFix", class_1208.field_5727, "Sign");
	}

	private Dynamic<?> method_15582(Dynamic<?> dynamic, String string) {
		String string2 = dynamic.get(string).asString("");
		class_2561 lv = null;
		if (!"null".equals(string2) && !StringUtils.isEmpty(string2)) {
			if (string2.charAt(0) == '"' && string2.charAt(string2.length() - 1) == '"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
				try {
					lv = class_3518.method_15279(field_15827, string2, class_2561.class, true);
					if (lv == null) {
						lv = new class_2585("");
					}
				} catch (JsonParseException var8) {
				}

				if (lv == null) {
					try {
						lv = class_2561.class_2562.method_10877(string2);
					} catch (JsonParseException var7) {
					}
				}

				if (lv == null) {
					try {
						lv = class_2561.class_2562.method_10873(string2);
					} catch (JsonParseException var6) {
					}
				}

				if (lv == null) {
					lv = new class_2585(string2);
				}
			} else {
				lv = new class_2585(string2);
			}
		} else {
			lv = new class_2585("");
		}

		return dynamic.set(string, dynamic.createString(class_2561.class_2562.method_10867(lv)));
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			dynamic = this.method_15582(dynamic, "Text1");
			dynamic = this.method_15582(dynamic, "Text2");
			dynamic = this.method_15582(dynamic, "Text3");
			return this.method_15582(dynamic, "Text4");
		});
	}
}
