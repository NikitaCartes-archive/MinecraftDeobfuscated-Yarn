package net.minecraft.datafixer.fix;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
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

	public static Dynamic<?> method_56629(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.asString().result();
		if (optional.isEmpty()) {
			return dynamic;
		} else {
			String string = (String)optional.get();
			if (!string.isEmpty() && !string.equals("null")) {
				char c = string.charAt(0);
				char d = string.charAt(string.length() - 1);
				if (c == '"' && d == '"' || c == '{' && d == '}' || c == '[' && d == ']') {
					try {
						JsonElement jsonElement = JsonParser.parseString(string);
						if (jsonElement.isJsonPrimitive()) {
							return text(dynamic.getOps(), jsonElement.getAsString());
						}

						return dynamic.createString(JsonHelper.toSortedString(jsonElement));
					} catch (JsonParseException var6) {
					}
				}

				return text(dynamic.getOps(), string);
			} else {
				return empty(dynamic.getOps());
			}
		}
	}
}
