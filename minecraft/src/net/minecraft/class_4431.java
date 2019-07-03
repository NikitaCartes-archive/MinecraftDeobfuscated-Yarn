package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4431 {
	public static String method_21547(String string, JsonObject jsonObject, String string2) {
		JsonElement jsonElement = jsonObject.get(string);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? string2 : jsonElement.getAsString();
		} else {
			return string2;
		}
	}

	public static int method_21545(String string, JsonObject jsonObject, int i) {
		JsonElement jsonElement = jsonObject.get(string);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? i : jsonElement.getAsInt();
		} else {
			return i;
		}
	}

	public static long method_21546(String string, JsonObject jsonObject, long l) {
		JsonElement jsonElement = jsonObject.get(string);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? l : jsonElement.getAsLong();
		} else {
			return l;
		}
	}

	public static boolean method_21548(String string, JsonObject jsonObject, boolean bl) {
		JsonElement jsonElement = jsonObject.get(string);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? bl : jsonElement.getAsBoolean();
		} else {
			return bl;
		}
	}

	public static Date method_21544(String string, JsonObject jsonObject) {
		JsonElement jsonElement = jsonObject.get(string);
		return jsonElement != null ? new Date(Long.parseLong(jsonElement.getAsString())) : new Date();
	}
}
