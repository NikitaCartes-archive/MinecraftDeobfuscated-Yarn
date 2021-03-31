/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(MinecraftClient.getInstance().getNetworkProxy());
    private static final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
    public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>(){

        @Override
        public GameProfile load(String string) throws Exception {
            GameProfile gameProfile = sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false);
            if (gameProfile == null) {
                throw new Exception("Couldn't get profile");
            }
            return gameProfile;
        }

        @Override
        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((String)object);
        }
    });
    private static final int field_32129 = 60;
    private static final int field_32130 = 3600;
    private static final int field_32131 = 86400;

    public static String uuidToName(String uuid) throws Exception {
        GameProfile gameProfile = gameProfileCache.get(uuid);
        return gameProfile.getName();
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(String uuid) {
        try {
            GameProfile gameProfile = gameProfileCache.get(uuid);
            return sessionService.getTextures(gameProfile, false);
        } catch (Exception exception) {
            return Maps.newHashMap();
        }
    }

    public static String convertToAgePresentation(long l) {
        if (l < 0L) {
            return "right now";
        }
        long m = l / 1000L;
        if (m < 60L) {
            return (m == 1L ? "1 second" : m + " seconds") + " ago";
        }
        if (m < 3600L) {
            long n = m / 60L;
            return (n == 1L ? "1 minute" : n + " minutes") + " ago";
        }
        if (m < 86400L) {
            long n = m / 3600L;
            return (n == 1L ? "1 hour" : n + " hours") + " ago";
        }
        long n = m / 86400L;
        return (n == 1L ? "1 day" : n + " days") + " ago";
    }

    public static String method_25282(Date date) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
    }
}

