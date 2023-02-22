/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class JsonUtils {
    public static <T> T get(String key, JsonObject node, Function<JsonObject, T> deserializer) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        if (!jsonElement.isJsonObject()) {
            throw new IllegalStateException("Required property " + key + " was not a JsonObject as espected");
        }
        return deserializer.apply(jsonElement.getAsJsonObject());
    }

    public static String getString(String key, JsonObject node) {
        String string = JsonUtils.getStringOr(key, node, null);
        if (string == null) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return string;
    }

    @Nullable
    public static String getStringOr(String key, JsonObject node, @Nullable String defaultValue) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsString();
        }
        return defaultValue;
    }

    @Nullable
    public static UUID getUuidOr(String key, JsonObject node, @Nullable UUID defaultValue) {
        String string = JsonUtils.getStringOr(key, node, null);
        if (string == null) {
            return defaultValue;
        }
        return UUID.fromString(string);
    }

    public static int getIntOr(String key, JsonObject node, int defaultValue) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsInt();
        }
        return defaultValue;
    }

    public static long getLongOr(String key, JsonObject node, long defaultValue) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsLong();
        }
        return defaultValue;
    }

    public static boolean getBooleanOr(String key, JsonObject node, boolean defaultValue) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsBoolean();
        }
        return defaultValue;
    }

    public static Date getDateOr(String key, JsonObject node) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return new Date(Long.parseLong(jsonElement.getAsString()));
        }
        return new Date();
    }
}

