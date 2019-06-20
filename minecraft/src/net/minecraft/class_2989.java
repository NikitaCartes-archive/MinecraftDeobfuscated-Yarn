package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2989 extends class_4309 {
	private static final Logger field_13406 = LogManager.getLogger();
	private static final Gson field_13405 = new GsonBuilder()
		.registerTypeHierarchyAdapter(class_161.class_162.class, (JsonDeserializer<class_161.class_162>)(jsonElement, type, jsonDeserializationContext) -> {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "advancement");
			return class_161.class_162.method_692(jsonObject, jsonDeserializationContext);
		})
		.registerTypeAdapter(class_170.class, new class_170.class_172())
		.registerTypeHierarchyAdapter(class_2561.class, new class_2561.class_2562())
		.registerTypeHierarchyAdapter(class_2583.class, new class_2583.class_2584())
		.registerTypeAdapterFactory(new class_3530())
		.create();
	private class_163 field_13404 = new class_163();

	public class_2989() {
		super(field_13405, "advancements");
	}

	protected void method_20724(Map<class_2960, JsonObject> map, class_3300 arg, class_3695 arg2) {
		Map<class_2960, class_161.class_162> map2 = Maps.<class_2960, class_161.class_162>newHashMap();
		map.forEach((argx, jsonObject) -> {
			try {
				class_161.class_162 lvx = field_13405.fromJson(jsonObject, class_161.class_162.class);
				map2.put(argx, lvx);
			} catch (IllegalArgumentException | JsonParseException var4x) {
				field_13406.error("Parsing error loading custom advancement {}: {}", argx, var4x.getMessage());
			}
		});
		class_163 lv = new class_163();
		lv.method_711(map2);

		for (class_161 lv2 : lv.method_715()) {
			if (lv2.method_686() != null) {
				class_194.method_852(lv2);
			}
		}

		this.field_13404 = lv;
	}

	@Nullable
	public class_161 method_12896(class_2960 arg) {
		return this.field_13404.method_716(arg);
	}

	public Collection<class_161> method_12893() {
		return this.field_13404.method_712();
	}
}
