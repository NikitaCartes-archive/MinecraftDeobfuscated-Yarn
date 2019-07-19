package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class JsonUtils {
	public static String getStringOr(String key, JsonObject node, String defaultValue) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsString();
		} else {
			return defaultValue;
		}
	}

	public static int getIntOr(String key, JsonObject node, int defaultValue) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsInt();
		} else {
			return defaultValue;
		}
	}

	public static long getLongOr(String key, JsonObject node, long defaultValue) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsLong();
		} else {
			return defaultValue;
		}
	}

	public static boolean getBooleanOr(String key, JsonObject node, boolean defaultValue) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsBoolean();
		} else {
			return defaultValue;
		}
	}

	public static Date getDateOr(String key, JsonObject node) {
		JsonElement jsonElement = node.get(key);
		return jsonElement != null ? new Date(Long.parseLong(jsonElement.getAsString())) : new Date();
	}
}
