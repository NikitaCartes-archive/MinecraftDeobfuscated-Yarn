package net.minecraft.datafixer.fix;

import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.JsonHelper;

public class TextFixes {
	private static final String EMPTY_TEXT = text("");

	public static <T> Dynamic<T> text(DynamicOps<T> ops, String string) {
		String string2 = text(string);
		return new Dynamic<>(ops, ops.createString(string2));
	}

	public static <T> Dynamic<T> empty(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createString(EMPTY_TEXT));
	}

	private static String text(String string) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("text", string);
		return JsonHelper.toSortedString(jsonObject);
	}

	public static <T> Dynamic<T> translate(DynamicOps<T> ops, String key) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("translate", key);
		return new Dynamic<>(ops, ops.createString(JsonHelper.toSortedString(jsonObject)));
	}

	public static <T> Dynamic<T> fixText(Dynamic<T> dynamic) {
		return DataFixUtils.orElse(dynamic.asString().map(string -> text(dynamic.getOps(), string)).result(), dynamic);
	}
}
