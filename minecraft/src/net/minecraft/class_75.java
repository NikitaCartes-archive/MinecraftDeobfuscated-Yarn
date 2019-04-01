package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class class_75 {
	private static final Map<class_2960, class_79.class_81<?>> field_985 = Maps.<class_2960, class_79.class_81<?>>newHashMap();
	private static final Map<Class<?>, class_79.class_81<?>> field_986 = Maps.<Class<?>, class_79.class_81<?>>newHashMap();

	private static void method_403(class_79.class_81<?> arg) {
		field_985.put(arg.method_423(), arg);
		field_986.put(arg.method_425(), arg);
	}

	static {
		method_403(class_69.method_395(new class_2960("alternatives"), class_65.class, class_65::new));
		method_403(class_69.method_395(new class_2960("sequence"), class_93.class, class_93::new));
		method_403(class_69.method_395(new class_2960("group"), class_72.class, class_72::new));
		method_403(new class_73.class_74());
		method_403(new class_77.class_78());
		method_403(new class_83.class_84());
		method_403(new class_67.class_68());
		method_403(new class_91.class_92());
	}

	public static class class_76 implements JsonDeserializer<class_79>, JsonSerializer<class_79> {
		public class_79 method_407(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "entry");
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "type"));
			class_79.class_81<?> lv2 = (class_79.class_81<?>)class_75.field_985.get(lv);
			if (lv2 == null) {
				throw new JsonParseException("Unknown item type: " + lv);
			} else {
				class_209[] lvs = class_3518.method_15283(jsonObject, "conditions", new class_209[0], jsonDeserializationContext, class_209[].class);
				return lv2.method_424(jsonObject, jsonDeserializationContext, lvs);
			}
		}

		public JsonElement method_408(class_79 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			class_79.class_81<class_79> lv = method_406(arg.getClass());
			jsonObject.addProperty("type", lv.method_423().toString());
			if (!ArrayUtils.isEmpty((Object[])arg.field_988)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.field_988));
			}

			lv.method_422(jsonObject, arg, jsonSerializationContext);
			return jsonObject;
		}

		private static class_79.class_81<class_79> method_406(Class<?> class_) {
			class_79.class_81<?> lv = (class_79.class_81<?>)class_75.field_986.get(class_);
			if (lv == null) {
				throw new JsonParseException("Unknown item type: " + class_);
			} else {
				return (class_79.class_81<class_79>)lv;
			}
		}
	}
}
