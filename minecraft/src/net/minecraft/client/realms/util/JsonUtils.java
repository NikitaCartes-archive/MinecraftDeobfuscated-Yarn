package net.minecraft.client.realms.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UndashedUuid;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class JsonUtils {
	public static <T> T get(String key, JsonObject node, Function<JsonObject, T> deserializer) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new IllegalStateException("Missing required property: " + key);
		} else if (!jsonElement.isJsonObject()) {
			throw new IllegalStateException("Required property " + key + " was not a JsonObject as espected");
		} else {
			return (T)deserializer.apply(jsonElement.getAsJsonObject());
		}
	}

	@Nullable
	public static <T> T getNullable(String key, JsonObject node, Function<JsonObject, T> deserializer) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return null;
		} else if (!jsonElement.isJsonObject()) {
			throw new IllegalStateException("Required property " + key + " was not a JsonObject as espected");
		} else {
			return (T)deserializer.apply(jsonElement.getAsJsonObject());
		}
	}

	public static String getString(String key, JsonObject node) {
		String string = getNullableStringOr(key, node, null);
		if (string == null) {
			throw new IllegalStateException("Missing required property: " + key);
		} else {
			return string;
		}
	}

	public static String getStringOr(String key, JsonObject node, String defaultValue) {
		return getNullableStringOr(key, node, defaultValue);
	}

	@Nullable
	public static String getNullableStringOr(String key, JsonObject node, @Nullable String defaultValue) {
		JsonElement jsonElement = node.get(key);
		if (jsonElement != null) {
			return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsString();
		} else {
			return defaultValue;
		}
	}

	@Nullable
	public static UUID getUuidOr(String key, JsonObject node, @Nullable UUID defaultValue) {
		String string = getNullableStringOr(key, node, null);
		return string == null ? defaultValue : UndashedUuid.fromStringLenient(string);
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
