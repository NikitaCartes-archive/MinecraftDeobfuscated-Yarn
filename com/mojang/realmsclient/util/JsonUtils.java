/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class JsonUtils {
    public static String getStringOr(String key, JsonObject node, String defaultValue) {
        JsonElement jsonElement = node.get(key);
        if (jsonElement != null) {
            return jsonElement.isJsonNull() ? defaultValue : jsonElement.getAsString();
        }
        return defaultValue;
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

