package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_394 {
	field_2312("bitmap", class_386.class_387::method_2037),
	field_2317("ttf", class_396::method_2059),
	field_2313("legacy_unicode", class_391.class_392::method_2046);

	private static final Map<String, class_394> field_2311 = class_156.method_654(Maps.<String, class_394>newHashMap(), hashMap -> {
		for (class_394 lv : values()) {
			hashMap.put(lv.field_2314, lv);
		}
	});
	private final String field_2314;
	private final Function<JsonObject, class_389> field_2315;

	private class_394(String string2, Function<JsonObject, class_389> function) {
		this.field_2314 = string2;
		this.field_2315 = function;
	}

	public static class_394 method_2048(String string) {
		class_394 lv = (class_394)field_2311.get(string);
		if (lv == null) {
			throw new IllegalArgumentException("Invalid type: " + string);
		} else {
			return lv;
		}
	}

	public class_389 method_2047(JsonObject jsonObject) {
		return (class_389)this.field_2315.apply(jsonObject);
	}
}
