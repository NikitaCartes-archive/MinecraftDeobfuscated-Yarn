/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File skinCacheDir, final MinecraftSessionService sessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = skinCacheDir;
        this.sessionService = sessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            @Override
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String string) {
                GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
                gameProfile.getProperties().put("textures", new Property("textures", string, ""));
                try {
                    return sessionService.getTextures(gameProfile, false);
                } catch (Throwable throwable) {
                    return ImmutableMap.of();
                }
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((String)object);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(profileTexture, type, null);
    }

    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = new Identifier("skins/" + string);
        AbstractTexture abstractTexture = this.textureManager.method_34590(identifier, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (callback != null) {
                    callback.onSkinTextureAvailable(type, identifier, profileTexture);
                }
            });
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        } else if (callback != null) {
            callback.onSkinTextureAvailable(type, identifier, profileTexture);
        }
        return identifier;
    }

    public void loadSkin(GameProfile profile, SkinTextureAvailableCallback callback, boolean requireSecure) {
        Runnable runnable = () -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(profile, requireSecure));
            } catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                profile.getProperties().clear();
                if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(this.sessionService.getTextures(profile, false));
                } else {
                    this.sessionService.fillProfileProperties(profile, requireSecure);
                    try {
                        map.putAll(this.sessionService.getTextures(profile, requireSecure));
                    } catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(MinecraftProfileTexture.Type.SKIN, MinecraftProfileTexture.Type.CAPE).forEach(type -> {
                if (map.containsKey(type)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(type), (MinecraftProfileTexture.Type)((Object)((Object)((Object)((Object)type)))), callback);
                }
            })));
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
        Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
        if (property == null) {
            return ImmutableMap.of();
        }
        return this.skinCache.getUnchecked(property.getValue());
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

