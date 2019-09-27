/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = file;
        this.sessionService = minecraftSessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> method_4657(GameProfile gameProfile) throws Exception {
                try {
                    return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
                } catch (Throwable throwable) {
                    return Maps.newHashMap();
                }
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.method_4657((GameProfile)object);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(minecraftProfileTexture, type, null);
    }

    public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureAvailableCallback skinTextureAvailableCallback) {
        String string = Hashing.sha1().hashUnencodedChars(minecraftProfileTexture.getHash()).toString();
        Identifier identifier = new Identifier("skins/" + string);
        AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
        if (abstractTexture != null) {
            if (skinTextureAvailableCallback != null) {
                skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
            }
        } else {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, minecraftProfileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (skinTextureAvailableCallback != null) {
                    skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
                }
            });
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        }
        return identifier;
    }

    public void loadSkin(GameProfile gameProfile, SkinTextureAvailableCallback skinTextureAvailableCallback, boolean bl) {
        Runnable runnable = () -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(gameProfile, bl));
            } catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                gameProfile.getProperties().clear();
                if (gameProfile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    gameProfile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(this.sessionService.getTextures(gameProfile, false));
                } else {
                    this.sessionService.fillProfileProperties(gameProfile, bl);
                    try {
                        map.putAll(this.sessionService.getTextures(gameProfile, bl));
                    } catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(MinecraftProfileTexture.Type.SKIN, MinecraftProfileTexture.Type.CAPE).forEach(type -> {
                if (map.containsKey(type)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(type), (MinecraftProfileTexture.Type)((Object)((Object)((Object)((Object)type)))), skinTextureAvailableCallback);
                }
            })));
        };
        SystemUtil.getServerWorkerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
        return this.skinCache.getUnchecked(gameProfile);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

