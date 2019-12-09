/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class UploadTokenCache {
    private static final Map<Long, String> tokenCache = Maps.newHashMap();

    public static String get(long worldId) {
        return tokenCache.get(worldId);
    }

    public static void invalidate(long world) {
        tokenCache.remove(world);
    }

    public static void put(long wid, String token) {
        tokenCache.put(wid, token);
    }
}

