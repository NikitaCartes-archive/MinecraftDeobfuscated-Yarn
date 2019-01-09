package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_790 {
	private final Map<String, class_807> field_4241 = Maps.<String, class_807>newLinkedHashMap();
	private class_816 field_4240;

	public static class_790 method_3424(class_790.class_791 arg, Reader reader) {
		return class_3518.method_15276(arg.field_4243, reader, class_790.class);
	}

	public class_790(Map<String, class_807> map, class_816 arg) {
		this.field_4240 = arg;
		this.field_4241.putAll(map);
	}

	public class_790(List<class_790> list) {
		class_790 lv = null;

		for (class_790 lv2 : list) {
			if (lv2.method_3422()) {
				this.field_4241.clear();
				lv = lv2;
			}

			this.field_4241.putAll(lv2.field_4241);
		}

		if (lv != null) {
			this.field_4240 = lv.field_4240;
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof class_790) {
				class_790 lv = (class_790)object;
				if (this.field_4241.equals(lv.field_4241)) {
					return this.method_3422() ? this.field_4240.equals(lv.field_4240) : !lv.method_3422();
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.field_4241.hashCode() + (this.method_3422() ? this.field_4240.hashCode() : 0);
	}

	public Map<String, class_807> method_3423() {
		return this.field_4241;
	}

	public boolean method_3422() {
		return this.field_4240 != null;
	}

	public class_816 method_3421() {
		return this.field_4240;
	}

	@Environment(EnvType.CLIENT)
	public static final class class_791 {
		protected final Gson field_4243 = new GsonBuilder()
			.registerTypeAdapter(class_790.class, new class_790.class_792())
			.registerTypeAdapter(class_813.class, new class_813.class_814())
			.registerTypeAdapter(class_807.class, new class_807.class_808())
			.registerTypeAdapter(class_816.class, new class_816.class_817(this))
			.registerTypeAdapter(class_819.class, new class_819.class_820())
			.create();
		private class_2689<class_2248, class_2680> field_4242;

		public class_2689<class_2248, class_2680> method_3425() {
			return this.field_4242;
		}

		public void method_3426(class_2689<class_2248, class_2680> arg) {
			this.field_4242 = arg;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_792 implements JsonDeserializer<class_790> {
		public class_790 method_3428(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, class_807> map = this.method_3429(jsonDeserializationContext, jsonObject);
			class_816 lv = this.method_3427(jsonDeserializationContext, jsonObject);
			if (!map.isEmpty() || lv != null && !lv.method_3520().isEmpty()) {
				return new class_790(map, lv);
			} else {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, class_807> method_3429(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<String, class_807> map = Maps.<String, class_807>newHashMap();
			if (jsonObject.has("variants")) {
				JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "variants");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), class_807.class));
				}
			}

			return map;
		}

		@Nullable
		protected class_816 method_3427(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			if (!jsonObject.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = class_3518.method_15261(jsonObject, "multipart");
				return jsonDeserializationContext.deserialize(jsonArray, class_816.class);
			}
		}
	}
}
